# -*- coding: utf8 -*-
from flask import Flask, jsonify
import libraries.mongodb as mongodb
from modules.errors import BaseException


def createApp(config, url_prefix=None):
    from app.libraries import loggerFactory
    # FIXME static directives should be set in config files, not here
    app = Flask(
        __name__, static_folder="static/site", static_url_path="/static")

    mongodb.setDefaultConfig(config)
    app.config.from_object(config)

    from routes import jobs
    from routes import accounts
    from routes import adminJobs
    from routes import crawlers
    from routes import site

    app.register_blueprint(
        site.getBlueprint(config), url_prefix=url_prefix)

    url_prefix += "/api/v1"
    loggerFactory.get().warning(url_prefix)
    app.register_blueprint(
        jobs.getBlueprint(config), url_prefix=url_prefix)
    app.register_blueprint(
        accounts.getBlueprint(config), url_prefix=url_prefix)
    app.register_blueprint(
        adminJobs.getBlueprint(config), url_prefix=url_prefix)
    app.register_blueprint(
        crawlers.getBlueprint(config), url_prefix=url_prefix)

    @app.errorhandler(BaseException)
    def handle_exceptions(error):
        return jsonify({"status": error.__json__()})

    @app.errorhandler(Exception)
    def handle_exceptions(error):
        loggerFactory.get().error(error, exc_info=True)
        return jsonify({"status": {
            "type": "UncaughtError - " + error.__class__.__name__,
            "code": 500,
            "message": "something went wrong, and a notification about this" +
                    " just sent to the manager."}}), 500

    # @app.route('/', methods=['GET'])
    # def get():
    #     return "Welcome to FLJ API END-POINT. At the moment, there\
    #      is not any documentation. Sorry."
    return app


# def createCeleryApp(config):
#     mongodb.setDefaultConfig(config)
#     celery.setCeleryConfig(config)
#     mailer.setMailerConfig(config)
#     app = celery.getCelery()
#     from livechat.proxies import widgets
#     return app
