# -*- coding: utf8 -*-
from flask import Blueprint, jsonify
from app.libraries import response
from app.modules.errors import WrongArgumentException
from app.libraries.routerDecorators import jsonizeRequest
from app.modules.accounts import Accounts


def getBlueprint(config):
    app = Blueprint('accounts', __name__)

    @app.route('/accounts/', methods=['POST'])
    @jsonizeRequest
    def register(data):
        try:
            account = data["account"]
        except Exception, e:
            raise WrongArgumentException(
                "account data not found in the request body")

        account = Accounts(config).insert(account)
        return jsonify(response.make(20, {"account": account}).__json__())

    @app.route('/accounts/<accountId>', methods=['GET'])
    def getOne(accountId):
        account = Accounts(config).getOne(accountId)
        return jsonify(response.make(20, {"account": account}).__json__())

    @app.route('/accounts/gcmids/<gcmId>', methods=['GET'])
    def getByGcmId(gcmId):
        account = Accounts(config).getByGcm(gcmId)
        return jsonify(response.make(20, {"account": account}).__json__())

    @app.route('/accounts/<accountId>/alarms/', methods=['PUT'])
    @jsonizeRequest
    def insertAlarm(accountId, data):
        try:
            alarm = data["alarm"]
        except Exception, e:
            raise WrongArgumentException(
                "alarm data not found in the request body")

        Accounts(config).insertAlarm(accountId, alarm)
        return jsonify(response.make(20, {"alarm": alarm}).__json__())

    @app.route('/accounts/<accountId>/alarms/<alarmId>', methods=['DELETE'])
    @jsonizeRequest
    def removeAlarm(accountId, alarmId, data):
        try:
            alarm = data["alarm"]
        except Exception, e:
            raise WrongArgumentException(
                "alarm data not found in the request body")

        Accounts(config).removeAlarm(accountId, alarmId)
        alarm = {"id": alarmId}
        return jsonify(response.make(20, {"alarm": alarm}).__json__())

    @app.route('/dummies/accounts', methods=['GET'])
    def getDummies():
        return response.make(20, {"accounts": [
              {
                "createdAt": "2015-04-25 12:30:12",
                "lastLogin": "2015-04-25 15:41:27",
                "fname": "Mustafa",
                "lname": "Atik",
                "email": "mm@aasscsaccccmm.com",
                "gcmId": None,
                "alarms": [
                    {
                        "createdAt": "2015-04-25 15:30:12",
                        "location": {
                            "country": "United Kingdom",
                            "state": "",
                            "city": "London",
                            },
                        "keywords": [
                            "speaker",
                            "scientist",
                            "developer"
                        ]
                    }
                ]
              }
            ]})

    return app
