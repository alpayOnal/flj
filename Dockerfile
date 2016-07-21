FROM python:3.5.2
ADD . /flj
WORKDIR /flj
RUN pip install -r requirements.txt
CMD python manage.py migrate
RUN apt-get install phpmyadmin
