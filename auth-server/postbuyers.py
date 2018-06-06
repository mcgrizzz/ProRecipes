import requests
import logging
import datetime
import time
from lxml import html
import re, hashlib

logging.basicConfig(filename='log.log')#,level=logging.DEBUG)

buyers = []
running = True
base_url = "http://auth.drepic.xyz/auth/req"
base_url_local = "http://localhost/req"

def getDefaultCookies():
    r = requests.get("https://www.spigotmc.org/", cookies={"":""})
    return r.cookies

def getBuyersListHtml():
     r = requests.post("https://www.spigotmc.org/login/login", cookies=getDefaultCookies(), data={'login': "USERNAME", 'password':
     "PASSWORD", "register": "0", "remember" : "1", "cookie_check": "1", "_xfToken": "",  "redirect": "https://www.spigotmc.org/resources/9039/buyers"})
     #logging.info(r.status_code + " " +  r.reason)
     logging.info("Fetched from spigot..")
     return r.content


def numerate(str):
    return re.sub('[^0-9]','', str.split(".")[1])

def updateBuyers():
    tree = html.fromstring(getBuyersListHtml())
    links = tree.xpath('//*[@id="content"]/div/div/div[2]/div/div/div[3]/div/ol/li/div[2]/h3/a/@href')
    tempbuyers = []
    for x in links:
        buyer = numerate(x)
        tempbuyers.append(buyer)
        #print(buyer)
        if((buyer not in buyers)):
            buyers.append(buyer)
            postBuyer(buyer)

    removebuyers = []
    for x in buyers:
        if(x not in tempbuyers):
            removebuyers.append(x)

    for x in removebuyers:
        buyers.remove(x)

    #print(buyers)
def postBuyer(buyer_id):
    key = generate_key(13001, 9039)
    r = requests.get(base_url, params={'key' : str(key), 'cmd' : "add_buyer", "buyer" : str(buyer_id), "plugin" : "9039"})

def generate_key(buyer_id, plugin):
    md = hashlib.sha256(str(buyer_id*plugin))
    return md.hexdigest()

def main():
    #while running:
        updateBuyers()
        #time.sleep(600)
        logging.info("Current Buyers size: ")
        print("Current Buyers size:")
        print(str(len(buyers)))
        logging.info(str(len(buyers)))

if __name__ == "__main__":
    main()
