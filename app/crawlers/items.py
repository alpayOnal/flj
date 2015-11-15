# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

import scrapy


class JobItem(scrapy.Item):
    title = scrapy.Field()
    description = scrapy.Field()
    salary = scrapy.Field()
    jobType = scrapy.Field()
    location = scrapy.Field()
    company = scrapy.Field()
    source = scrapy.Field()
    date = scrapy.Field()
    meta = scrapy.Field()
