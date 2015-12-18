# Rainbow [![Build Status](https://travis-ci.org/rafaelfiume/Rainbow.svg?branch=master)](https://travis-ci.org/rafaelfiume/Rainbow) [![Apache 2.0 License](https://img.shields.io/badge/license-Apache_2.0-blue.svg)](https://github.com/rafaelfiume/Rainbow/blob/master/LICENSE)

Executes black-box end-to-end tests in the Supplier stack in the staging environment. The results are available [here](http://rafaelfiume.github.io/Rainbow/).

The Supplier stack must be running in order to run Rainbow tests.

## Running the application locally

Build with:

    $mvn clean install

## Environment Variables

The following variables are used in this project:

* $SUPPLIER_URL points to Supplier in local or staging.
* $DATABASE_URL points to the database in local or staging.