# -*- coding: utf8 -*-


class GCMClient(object):
    """docstring for GCMClient"""
    def __init__(self, config):
        super(GCMClient, self).__init__()
        self.config = config

    def publish(self, gcmID, notification):
        print "publishing - gcm: {gcmID} {notification!r}".format(
            gcmID=gcmID, notification=notification)
        return
        raise NotImplementedError()
