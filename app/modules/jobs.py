# -*- coding: utf8 -*-
from app.libraries import loggerFactory
from app.libraries.mongodb import getDb
from bson.objectid import ObjectId
from app.modules.errors import (NotFoundException,
                                WrongArgumentException)
import arrow


def renameID(job):
    job["id"] = job["_id"]
    del job["_id"]
    return job


class Jobs(object):
    """This module handles operations related to job entries"""

    def __init__(self, config):
        super(Jobs, self).__init__()
        self.db = getDb()
        self.storage = self.db["jobs"]
        self.logger = loggerFactory.get()

    def insert(self, job):
        # schema validation is needed here
        if "id" in job:
            del job["id"]
        job["location"]["country"] = job["location"]["country"].lower()
        job["location"]["city"] = job["location"]["city"].lower()
        job["location"]["region"] = job["location"]["region"].lower()
        job["jobType"] = job["jobType"].lower()
        self.storage.insert(job)
        return renameID(job)

    def get(self, filtering=None, length=100):
        fids = filtering.get("ids", None)
        fsinceId = filtering.get("sinceId", None)
        fmaxId = filtering.get("maxId", None)
        flocation = filtering.get("location", {})
        ftitle = filtering.get("title", None)
        fdescription = filtering.get("description", None)
        fjobType = filtering.get("jobType", "")
        query = {"$or": []}

        if fids:
            query["_id"] = {"$in": fids}

        if fsinceId:
            query["_id"] = {"$gt": ObjectId(fsinceId)}

        if fmaxId:
            if query.get("_id"):
                query["_id"]["$lt"] = ObjectId(fmaxId)
            else:
                query["_id"] = {"$gt": ObjectId(fmaxId)}

        if flocation.get("country", None):
            query["location.country"] = flocation["country"].lower()
            query["location.city"] = flocation["city"].lower()
            if flocation.get("region", None):
                query["location.region"] = flocation["region"].lower()

        if ftitle:
            query["$or"].append({"title": {"$regex": ftitle, "$options": "i"}})

        if fdescription:
            query["$or"].append({"description": {
                "$regex": fdescription, "$options": "i"}})

        if fjobType:
            query["jobType"] = {"$regex": fjobType.lower()}

        if not query["$or"]:
            del query["$or"]

        self.logger.debug("jobs query: " + str(query))

        return self.storage.find(query).limit(length)

    def getOne(self, jobId):
        job = self.storage.find_one({"_id": ObjectId(jobId)})
        if not job:
            raise NotFoundException("job with id {} not found".format(jobId))
        return renameID(job)

    def delete(self, jobId):
        self.storage.remove({"_id": ObjectId(jobId)})

    def increaseView(self, jobId):
        return self.storage.update(
            {"_id": ObjectId(jobId)},
            {"$inc": {"stats.viewed": 1}})

    def increaseStar(self, jobId):
        return self.storage.update(
            {"_id": ObjectId(jobId)},
            {"$inc": {"stats.starred": 1}})

    def decreaseStar(self, jobId):
        return self.storage.update(
            {"_id": ObjectId(jobId)},
            {"$inc": {"stats.starred": -1}})
