# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html
from datetime import datetime
from modules.jobs import Jobs
from . import config


class JobPipeline(object):
    def process_item(self, item, spider):
        job = dict(item)
        job["date"] = job["date"].naive
        # TODO: remove location information in title if exists
        Jobs(config).insert(job)
        return item
