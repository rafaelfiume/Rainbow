# Rainbow [![Build Status](https://travis-ci.org/rafaelfiume/Rainbow.svg?branch=master)](https://travis-ci.org/rafaelfiume/Rainbow)

Executes black-box end-to-end tests in the Supplier stack in the staging environment. The results are available [here](http://rafaelfiume.github.io/Rainbow/).

The Supplier stack must be running in order to run Rainbow tests.

## Running the application locally

Build with:

    $mvn clean install

## Environment Variables

The following variables are used in this project:

#### Test
1) $SUPPLIER_STAGING_URL is mandatory and points to Supplier in staging.