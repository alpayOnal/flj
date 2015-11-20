import sys
import os


sys.path.insert(
    0,
    os.path.join(os.path.abspath(os.path.dirname(__file__)) + '/../'))


import configs
conf = configs.get()

from app.libraries import loggerFactory
loggerFactory.setConfig(conf, defaultName='')
logger = loggerFactory.get()

from app.builder import createApp
application = createApp(conf, url_prefix="")
application.secret_key = conf.SECRET_KEY

from launchers import monitor
monitor.start(interval=1.0)
