# -*- coding: utf-8 -*-
# Generated by Django 1.9.7 on 2016-07-24 12:21
from __future__ import unicode_literals

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('posts', '0002_auto_20160722_1547'),
    ]

    operations = [
        migrations.RenameField(
            model_name='jobpost',
            old_name='post_id',
            new_name='jobpost_id',
        ),
    ]
