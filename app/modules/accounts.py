# -*- coding: utf8 -*-
from app.libraries import loggerFactory
from app.libraries.mongodb import getDb
from bson.objectid import ObjectId
from app.modules.errors import (NotFoundException,
                                WrongArgumentException)
import arrow


def renameID(record):
    record["id"] = record["_id"]
    del record["_id"]
    return record


class Accounts(object):
    """This module handles operations related to user accounts"""

    def __init__(self, config):
        super(Accounts, self).__init__()
        self.db = getDb()
        self.storage = self.db["accounts"]
        self.logger = loggerFactory.get()

    def makeAlarmReadyToDb(self, alarm):
        alarm['location']['country'] = alarm['location']['country'].lower()
        alarm['location']['city'] = alarm['location']['city'].lower()
        alarm['location']['state'] = alarm['location']['state'].lower()
        return alarm

    def insert(self, account):
        # @TODO schema validation is needed here
        if "gcmId" not in account:
            raise WrongArgumentException("every account must have a gcmId.")

        account["createdAt"] = arrow.utcnow().naive

        if "alarms" not in account:
            account["alarms"] = []
        if "jobs" not in account:
            account["jobs"] = {"starred": []}

        for alarm in account['alarms']:
            alarm = self.makeAlarmReadyToDb(alarm)
        self.storage.insert(account)
        return renameID(account)

    def getOne(self, accountId):
        account = self.storage.find_one({"_id": ObjectId(accountId)})
        if not account:
            raise NotFoundException(
                "account with id {} not found".format(accountId))
        return renameID(account)

    def get(self, filtering=None):
        if not filtering:
            filtering = {}
        flocation = filtering.get("location", None)
        fkeyword = filtering.get("keyword", [])
        query = {}

        if flocation:
            query["location.country"] = flocation["country"].lower()
            query["location.city"] = flocation["city"].lower()
            if flocation.get("state", None):
                query["location.state"] = flocation["state"].lower()

        if fkeyword:
            query["keywords"] = {"$in": fkeyword}

        if flocation or fkeyword:
            query = {"alarms": {"$elemMatch": query}}

        self.logger.debug("account query: " + str(query))

        return self.storage.find(query)

    def getByGcm(self, gcmId):
        account = self.storage.find_one({"gcmId": gcmId})
        if not account:
            raise NotFoundException(
                "account with gcm {} not found".format(gcmId))
        return renameID(account)

    def insertAlarm(self, accountId, alarm):
        alarm = self.makeAlarmReadyToDb(alarm)
        alarm["createdAt"] = arrow.utcnow().naive
        self.storage.update(
            {
                "_id": ObjectId(accountId)
            },
            {
                "$push": {"alarms": alarm}
            })

    def removeAlarm(self, accountId, alarm):
        alarm = self.makeAlarmReadyToDb(alarm)
        location = alarm["location"]
        self.storage.update(
            {
                "_id": ObjectId(accountId)
            },
            {
                "$pull": {"alarms": {
                    "location.country": location["country"],
                    "location.state": location["state"],
                    "location.city": location["city"],
                    "keywords": alarm["keywords"]
                }}
            })

    def delete(self, accountId):
        self.storage.remove({"_id": ObjectId(accountId)})

    def insertStarredJob(self, accountId, jobId):
        jobId = ObjectId(jobId)
        self.storage.update(
            {
                "_id": ObjectId(accountId)
            },
            {
                "$push": {"jobs.starred": jobId}
            })

    def removeStarredJob(self, accountId, jobId):
        jobId = ObjectId(jobId)
        self.storage.update(
            {
                "_id": ObjectId(accountId)
            },
            {
                "$pull": {"jobs.starred": jobId}
            })

    # @TODO: unittest
    def saveNotifiactionStatus(self, notification):
        self.storage.update(
            {
                "_id": ObjectId(accountId)
            },
            {
                "$push": {"notifications": notification}
            })
