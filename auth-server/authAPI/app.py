from flask import Flask, jsonify, request
import json, datetime, time, os, hashlib
from threading import Timer


app = Flask(__name__)

ip_limit = 3; #limit of ips per buyer


buyers = {"9039" : ["13001"]} # stores all buyers
checked = {"9039" : {}}  #stores plugins that have checked
ips = {"9039" : {}} #stores all ips being used by a buyer
ip_whitelist = {"9039" : [{}]} #all whitelisted ips for a buyer that I add manually
blacklist = {"9039" : []}

running = False

changed = 0
checking = False

raw_buyers = None
raw_ips = None
raw_checked = None
raw_whitelist = None
raw_blacklist = None

class RepeatedTimer(object):
    def __init__(self, interval, function, *args, **kwargs):
        self._timer     = None
        self.interval   = interval
        self.function   = function
        self.args       = args
        self.kwargs     = kwargs
        self.is_running = False

    def _run(self):
        self.is_running = False
        self.start()
        self.function(*self.args, **self.kwargs)

    def start(self):
        if not self.is_running:
            self._timer = Timer(self.interval, self._run)
            self._timer.start()
            self.is_running = True

    def stop(self):
        self._timer.cancel()
        self.is_running = False

def save_data():
    with open(os.path.join(app.root_path, 'buyers.txt'), 'w') as outfile:
        outfile.write(json.dumps(buyers, outfile))
    with open(os.path.join(app.root_path, 'hosts.txt'), 'w') as outfile:
        outfile.write(json.dumps(ips, outfile))
    with open(os.path.join(app.root_path, 'checked.txt'), 'w') as outfile:
        outfile.write(json.dumps(checked, outfile))
    with open(os.path.join(app.root_path, 'whitelist.txt'), 'w') as outfile:
        outfile.write(json.dumps(ip_whitelist, outfile))
    with open(os.path.join(app.root_path, 'blacklist.txt'), 'w') as outfile:
        outfile.write(json.dumps(blacklist, outfile))

rt = RepeatedTimer(5, save_data)

@app.route("/req")
def login():
    operation = request.args.get("cmd")
    plugin = request.args.get("plugin")

    if(operation not in ["get_buyers", "is_buyer", "add_buyer", "remove_buyer", "can_host", "add_host", "remove_host", "is_hosting", "host_map", "data",
    "whitelist_add", "whitelist_remove", "blacklist_add", "blacklist_remove"]): #cehck if its a valid operation
        return jsonify({"error" : "no operation defined"})

    buyer_id = request.args.get("buyer")

    if(buyer_id == None):
        buyer_id = 0
    if(plugin == None):
        plugin = 0


    key = request.args.get("key")

    if(key == None):
        return jsonify({"error" : "not authorized"})

    if(operation == "is_buyer"):
        if(buyer_id == None):
            return jsonify({"error" : "no buyer defined"})

        if(buyer_id in buyers[plugin] and (buyer_id not in blacklist[plugin])):
            return jsonify({"is_buyer" : "true"}) #is a buyer, always return true
        elif(buyer_id in blacklist[plugin]):
            return jsonify({"is_buyer" : "false"})
        if(buyer_id in checked.get(str(plugin))):
            millis = int(round(time.time() * 1000))
            if(checked.get(str(plugin))[str(buyer_id)] < millis): #its already been 10 minutes :(
                return jsonify({"is_buyer" : "checked false"}) #the plugin checked on startup and it was false, but waited 10 mins and its still false
            else:
                return jsonify({"is_buyer" : "check again"})
        else:
            millis = int(round(time.time() * 1000)) + 600000
            checked[str(plugin)][str(buyer_id)] = millis #append new buyer id to checked list with time that check will no longer be valid and buyer is definately not a buyer
            global changed
            changed += 1;
            if(not checking):
                print("Not checking, checking now")
                check_save()
            return jsonify({"is_buyer" : "check again"}) #initial check on startup. is the first check therefore can add to checked list and determine if is a buyer

    auth_level = validate_key(int(buyer_id), int(plugin), key) #check authorization
    if(auth_level == 0):
        return jsonify({"error" : "not authorized"})
    if(operation == "data"):
        if(auth_level < 1):
            return jsonify({"error" : "not authorized"})
        return jsonify({"buyers" : buyers, "ips" : ips, "checked" : checked, "whitelist" : ip_whitelist, "blacklist" : blacklist})

    if(plugin not in ["9039"]):
        return jsonify({"error" : "incorrect plugin defined"})

    if(operation == "get_buyers"): #getlist of buyers
        if(auth_level < 1):
            return jsonify({"error" : "not authorized"})
        return jsonify({'buyers': buyers[plugin]})


    ip = request.remote_addr



    if(operation == "can_host"):
        if(auth_level < 1):
            return jsonify({"error" : "not authorized"})
        if(buyer_id == None):
            return jsonify({"error" : "no buyer defined"})
        if(buyer_id not in buyers[plugin]):
            return jsonify({"error" : "not a buyer"})
        if(buyer_id not in ips[plugin]):
            return jsonify({"can_host" : "true"})
        if(buyer_id in ips[plugin]):
            if(ip in ips[plugin][buyer_id]):
                return jsonify({"can_host" : "true"})
        if(buyer_id in ip_whitelist[plugin] and (ip in ip_whitelist[plugin][buyer_id])):
            return jsonify({"can_host" : "true"})
        return jsonify({"can_host" : (len(ips[plugin][buyer_id]) <= 2)})


    if(operation == "is_hosting"):
         if(auth_level < 1):
             return jsonify({"error" : "not authorized"})
         if(buyer_id == None):
             return jsonify({"error" : "no buyer defined"})
         if(buyer_id not in buyers[plugin]):
             return jsonify({"error" : "not a buyer"})
         if(buyer_id not in ips[plugin]):
             return jsonify({"hosting" : "false"})
         else:
            return jsonify({"hosting" : ip in ips[plugin][buyer_id]})

    if(operation == "host_map"):
        if(auth_level < 1):
            return jsonify({"error" : "not authorized"})
        full = request.args.get("full")
        if(full == None or full == "false"):
            if(buyer_id == None):
                return jsonify({"error" : "no buyer defined"})
            if(buyer_id not in buyers[plugin]):
                return jsonify({"error" : "not a buyer"})
            if(buyer_id not in ips[plugin]):
                return jsonify({"error" : "user is not currently hosting"})
            else:
                return jsonify({"host_map" : ips[plugin][buyer_id]})
        else:
            return jsonify({"host_map" : ips[plugin]})



    if(buyer_id == None):
        return jsonify({"error" : "no buyer defined"})

    if(operation == "add_buyer"):
        if(auth_level != 3):
            return jsonify({"error" : "not authorized"})
        if(buyer_id in buyers[plugin]):
            return jsonify({"error" : "already a buyer"})
        buyers[plugin].append(buyer_id)
        global changed
        changed += 1;
        if(not checking):
            print("Not checking, checking now")
            check_save()
        return jsonify({"success" : "buyer added"})

    if(operation == "whitelist_add"):
        ip_given = request.args.get("ip")
        if(auth_level != 3):
            return jsonify({"error" : "not authorized"})
        if(ip_given == None):
            return jsonify({"error" : "no ip given"})
        if(buyer_id in ip_whitelist[plugin] and (ip_given in ip_whitelist[plugin][buyer_id])):
            return jsonify({"error" : "already a whitelisted ip"})
        if(buyer_id not in ip_whitelist[plugin]):
            ip_whitelist[plugin][buyer_id] = [ip_given];
        else:
            ip_whitelist[plugin][buyer_id].append(ip_given);
        global changed
        changed += 1;
        if(not checking):
            check_save()
        return jsonify({"success" : "whitelist added"})

    if(operation == "whitelist_remove"):
        ip_given = request.args.get("ip")
        if(auth_level != 3):
            return jsonify({"error" : "not authorized"})
        if(ip_given == None):
            return jsonify({"error" : "no ip given"})
        if(buyer_id not in ip_whitelist[plugin] or (ip_given not in ip_whitelist[plugin][buyer_id])):
            return jsonify({"error" : "not a whitelisted ip"})
        ip_whitelist[plugin][buyer_id].remove(ip_given)
        global changed
        changed += 1;
        if(not checking):
            check_save()
        return jsonify({"success" : "whitelist removed"})

    if(operation == "blacklist_add"):
        if(auth_level != 3):
            return jsonify({"error" : "not authorized"})
        if(buyer_id in blacklist[plugin]):
            return jsonify({"error" : "already a blacklisted"})
        blacklist[plugin].append(buyer_id)
        global changed
        changed += 1;
        if(not checking):
            check_save()
        return jsonify({"success" : "blacklist added"})

    if(operation == "blacklist_remove"):
        if(auth_level != 3):
            return jsonify({"error" : "not authorized"})
        if(buyer_id not in blacklist[plugin]):
            return jsonify({"error" : "not a blacklisted user"})
        blacklist[plugin].remove(buyer_id)
        global changed
        changed += 1;
        if(not checking):
            check_save()
        return jsonify({"success" : "blacklist removed"})


    if(operation == "remove_buyer"):
        if(auth_level != 3):
            return jsonify({"error" : "not authorized"})
        buyers[plugin].remove(buyer_id)
        global changed
        changed += 1;
        if(not checking):
            check_save()
        return jsonify({"success" : "buyer removed"})

    if(operation == "add_host"):
        if(auth_level < 1):
            return jsonify({"error" : "not authorized"})
        if(buyer_id not in ips[plugin]):
            if(plugin in ip_whitelist and buyer_id in ip_whitelist[plugin] and ip in ip_whitelist[plugin][buyer_id]):
                return jsonify({"success" : "buyer is now hosting"})
            ips[plugin].update({buyer_id : [ip]})
            global changed
            changed += 1;
            if(not checking):
                check_save()
            return jsonify({"success" : "buyer is now hosting"})
        else:
            if(ip in ips[plugin][buyer_id]):
                return jsonify({"error" : "buyer is already hosting"})
            if(plugin in ip_whitelist and buyer_id in ip_whitelist[plugin] and ip in ip_whitelist[plugin][buyer_id]):
                return jsonify({"success" : "buyer is now hosting"})
            if((len(ips[plugin][buyer_id]) <= 2)):
                ips[plugin][buyer_id].append(ip)
                global changed
                changed += 1;
                if(not checking):
                    check_save()
                return jsonify({"success" : "buyer is now hosting"})
            else:
                return jsonify({"error" : "too many hosts"})

    if(operation == "remove_host"):
        if(auth_level < 2):
            return jsonify({"error" : "not authorized"})
        if(plugin in ip_whitelist and buyer_id in ip_whitelist[plugin] and ip in ip_whitelist[plugin][buyer_id]):
            return jsonify({"success" : "ip removed from hosting"})
        if(buyer_id not in ips[plugin]):
            return jsonify({"error" : "user is not currently hosting"})
        else:
            if(ip in ips[plugin][buyer_id]):
                ips[plugin][buyer_id].remove(ip)
                global changed
                changed += 1;
                if(not checking):
                    check_save()
                return jsonify({"success" : "ip removed from hosting"})
            else:
                return jsonify({"error" : "ip is not currently hosting"})

def check_save():
    checking = True
    global changed
    last = changed
    Timer(.1, check_save_two, [last]).start()

def validate_key(usr, plugin, key):
    has = hashlib.sha256(str(usr*plugin))
    if(not has.hexdigest() == key):
        has = hashlib.sha256(str(13001*plugin))
        if(has.hexdigest() == key):
            return 3
        else:
            for key, value in list(buyers.items()):
                for buyer in value:
                    has = hashlib.sha256(str(int(key)*int(buyer)))
                    if(has.hexdigest() == key):
                        return 1
            return 0
    else:
        return 2

def check_save_two(last):
    global changed
    if(changed > last):
        check_save()
    else:
        checking = False
        save_data()

def save_thread():
    rt.start()

def load_from_files():
    global raw_buyers
    global raw_ips
    global raw_checked
    global raw_whitelist
    global raw_blacklist
    global ips
    global buyers
    global checked
    global ip_whitelist
    global blacklist
    try:
        with open(os.path.join(app.root_path, 'buyers.txt'), 'r') as fp:
            raw_buyers = json.load(fp)
    except:
        print("no buyers file")
    try:
        with open(os.path.join(app.root_path, 'hosts.txt'), 'r') as fpp:
            raw_ips = json.load(fpp)
    except:
        print("no hosts file")
    try:
        with open(os.path.join(app.root_path, 'checked.txt'), 'r') as fpp:
            raw_checked = json.load(fpp)
    except:
        print("no checked file")
    try:
        with open(os.path.join(app.root_path, 'whitelist.txt'), 'r') as fpp:
            raw_whitelist = json.load(fpp)
    except:
        print("no whitelist file")
    try:
        with open(os.path.join(app.root_path, 'blacklist.txt'), 'r') as fpp:
            raw_blacklist = json.load(fpp)
    except:
        print("no blacklist file")

    if(raw_buyers is not None and bool(raw_buyers)):
        buyers = raw_buyers
    if(raw_ips is not None and bool(raw_ips)):
        ips = raw_ips
    if(raw_checked is not None and bool(raw_checked)):
        checked = raw_checked
    if(raw_whitelist is not None and bool(raw_whitelist)):
        ip_whitelist = raw_whitelist
    if(raw_blacklist is not None and bool(raw_blacklist)):
        blacklist = raw_blacklist


if __name__ == '__main__':
    running = True
    save_thread()
    app.debug = True
    app.run(host='0.0.0.0', port=80)
    rt.stop()


running = True
app.debug = True
load_from_files()
#save_thread()
