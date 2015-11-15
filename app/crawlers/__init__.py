# -*- coding: utf8 -*-
import sys
import os


sys.path.insert(
    0,
    os.path.join(os.path.abspath(os.path.dirname(__file__)) + '/../../'))


import configs
config = configs.get()

from app.libraries import mongodb
mongodb.setDefaultConfig(config)