from django.contrib.auth.models import User
from django.db import models


class UserProfile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    gcm_id = models.TextField(max_length=200, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    started_jobs = models.ManyToManyField("JobPost")


class JobPost(models.Model):
    post_id = models.AutoField(primary_key=True)
    user = models.ForeignKey(
        UserProfile, on_delete=models.CASCADE, related_name='job_posts')
    # @TODO: imeplement later: status = models.choice
    title = models.TextField(max_length=255, blank=False)
    description = models.TextField(blank=False)
    created_at = models.DateTimeField(auto_now_add=True)


class Location(models.Model):
    city = models.TextField(max_length=25, blank=False)
    country = models.TextField(max_length=25, blank=False)
    post = models.ForeignKey(JobPost, on_delete=models.CASCADE)


# class SavedJobs(models.Model):
#
#     job = models.ForeignKey(
#         primary_key=True, to=JobPost, on_delete=models.CASCADE)
#     user = models.ForeignKey(
#         UserProfile, on_delete=models.CASCADE, related_name='job_posts')