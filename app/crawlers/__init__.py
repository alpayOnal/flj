# -*- coding: utf8 -*-
import sys
import os
import arrow
import pymongo

sys.path.insert(
    0,
    os.path.join(os.path.abspath(os.path.dirname(__file__)) + '/../../'))


import configs
config = configs.get()

from app.libraries import mongodb
mongodb.setDefaultConfig(config)


class CrawlerLogs(object):
    # TODO adds api documentation of this class, and unittest
    storage = mongodb.getDb()["crawlerLogs"]

    @classmethod
    def insert(cls, name, url, jobsFound=0, newJobs=0):
        entry = {
            "date": arrow.utcnow().naive,
            "name": name,
            "url": url,
            "jobsFound": jobsFound,
            "newJobs": newJobs
        }
        cls.storage.insert(entry)

    @classmethod
    def get(cls, filtering):
        dateStart = filtering.get(
            "dateStart", arrow.utcnow().replace(hours=-48).naive)
        dateEnd = filtering.get("dateEnd", arrow.utcnow().naive)
        name = filtering.get("name", None)
        url = filtering.get("url", None)

        query = {"$or": []}

        query["date"] = {"$gte": dateStart, "$lt": dateEnd}
        if name:
            query["$or"].append({"name": {
                "$regex": name, "$options": "i"}})
        if url:
            query["$or"].append({"url": {
                "$regex": url, "$options": "i"}})

        if not query["$or"]:
            del query["$or"]
        return cls.storage.find(query).sort(
            "date", pymongo.DESCENDING)
