# -*- coding: utf8 -*-
# from app.libraries import loggerFactory
# from app.libraries.mongodb import getDb
# from app.modules.errors import (NotFoundException,
#                                 WrongArgumentException)


# class NotificationManager(object):
#     """docstring for NotificationManager"""
#     def __init__(self, config):
#         super(NotificationManager, self).__init__()
#         self.config = config

#     def receiveJobs(self):
#         # indeksle
#         #
#         pass

#     def sendNotification(self, account):
#         pass

jobs = None
jobIndex = {}
wordIndex = {}


def insertJobs(cursor):
    global jobs
    jobs = cursor
    indexJobs()


def extractWords(text):
    return text.lower().split(" ")


def indexJobs():
    global jobIndex
    for job in jobs:
        jobId = job["_id"]
        words = extractWords(job["title"])
        for word in words:
            wordIndex = jobIndex.get(word, {})
            wordIndex[jobId] = True
            jobIndex[word] = wordIndex


def getSatisfyingJobs(alarm):
    haystack = jobIndex.keys()
    found = []
    for keyword in alarm["keywords"]:
        for word in haystack:
            if keyword.lower() in word:
                jobIDs = jobIndex[word].keys()
                found += jobIDs
    return list(set(found))


def findSatisfiedAlarms(accounts):
    from collections import defaultdict
    satisfiedAlarms = defaultdict(list)
    for account in accounts:
        for alarm in account['alarms']:
            jobIDs = getSatisfyingJobs(alarm)
            for jobId in jobIDs:
                satisfiedAlarms[jobId].append(alarm)
    return satisfiedAlarms


def sendNotifications(account, satisfiedAlarms):
    sendNotifications("program files")
    pass

if __name__ == '__main__':
    from pprint import pprint as pp
    cursor = [
        {
            "_id": 1,
            "title": "elma armut patates"
        }, {
            "_id": 2,
            "title": "patates domates patlican"
        }, {
            "_id": 3,
            "title": "patlican karpuz kavun"
        }, {
            "_id": 4,
            "title": "elma karpuz patates"
        }
    ]
    accounts = [{
        "_id": 1,
        "alarms": [
            {
                "keywords": ["armut"]
            },
            {
                "keywords": ["kavu"]
            }
        ]
    }]

    insertJobs(cursor)
    satisfiedAlarms = dict(findSatisfiedAlarms(accounts))
    pp(satisfiedAlarms)
    sendNotifications(account, satisfiedAlarms)
