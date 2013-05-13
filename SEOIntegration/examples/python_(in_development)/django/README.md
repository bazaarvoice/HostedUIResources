Python BV SEO SDK using Django
========

BV SEO SDK for Python implemented using the [Django](https://www.djangoproject.com/) web framework.

Prerequisites:
-------------

* Django project

How to use:
-----------

* Copy the files from `./example/bvseosdk` into `$DJANGOPATH/$YOUR_DJANGO_PROJECT/`
* Add `'bvseosdk'` to your `INSTALLED_APPS` in your project `settings.py`
* Add a URL pattern to your project `urls.py` that maps to the `home` view in the `bvseosdk` app.
* Run Django: `python manage.py runserver` and navigate to your URL.