# -*- coding: utf8 -*-
from app.crawlers import CrawlerLogs
from app.modules.adminAuth import requires_admin
from flask import Blueprint, jsonify, request
from app.libraries import response


def getBlueprint(config):
    app = Blueprint('crawlers', __name__)

    @app.route("/admin/crawlers/logs/")
    @requires_admin
    def getLogs():
        # TODO: adds to api documentation
        filtering={
            "name": request.args.get("name", None),
            "url": request.args.get("url", None)
        }
        result = list(CrawlerLogs.get(filtering))
        for i in result:
            del i["_id"]

        return jsonify(response.make(20, {
            "filtering": filtering,
            "logs": result}).__json__())

    return app


