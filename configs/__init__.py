# -*- coding: utf8 -*-
import os
import sys
import default

defaultEnv = "prod"
enforcingEnv = None
ENV_VAR_NAME = "FLJ_ENV"


def get(env=None):
    """
    Bu metod, var sayilan degeri 'prod' olan 'ENV_VAR_NAME' isimli ortam
    degiskeniyle belirtilen konfigurasyon dosyasini yukler.

    Belirtilen konfigurasyon dosyasini app/configs dizininde
    barindirilmali ve default.py'dakiler gibi sinif olarak sunulmali.

    app/configs dizinindeki default.py ve __init__.py haric indeki
    dosyalar guvenlik sebebiyle surum kontrolu olan "git" tarafindan
    takip edilmeyecektir. bkz: app/.gitignore

    Default konfigorasyonlar sunlardir:
      * default.beta
      * default.dev
      * default.test

    """

    # inserting the current directory into the the path
    sys.path.insert(
        0,
        os.path.join(os.path.abspath(os.path.dirname(__file__))))

    ENV = env or enforcingEnv or os.getenv(ENV_VAR_NAME, defaultEnv)

    print('ENVIRONMENT: {}'.format(ENV))

    if ENV == 'default.beta':
        conf = default.Beta()
    if ENV == 'default.dev':
        conf = default.Dev()
    elif ENV == 'default.test':
        conf = default.Test()
    else:
        try:
            imported = __import__(ENV)
            conf = imported.Config()
        except Exception, e:
            raise Exception(
                ('Config module "{}" not found. Maybe you did '
                    'forget setting {} environment variable.')
                .format(ENV, ENV_VAR_NAME))

    return conf
