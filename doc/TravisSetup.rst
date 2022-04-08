..
   Copyright 2021-2022 MicroEJ Corp. All rights reserved.
   Use of this source code is governed by a BSD-style license that can be found with this software.

Seting up TravisCI
==================

Link TravisCI with the project
------------------------------
See [1]_

Go to Travis-ci.com and Sign up with GitHub.


Create access token for deploy
------------------------------
See [2]_

Connected to your account on GitHub.com :

Go to Settings
 -> Developer settings
  -> Personal access tokens
   -> Click on Generate new token.
     Select *repo* (*repo:invite* not needed for public repo)
 
**Write down the token : it isn't saved anywhere on GitHub!**
      
Secure the token
----------------
See [3]_

**Dependencies**

.. code-block:: text

   sudo apt install ruby ruby-dev
   gem install travis

**Setup**

.. code-block:: text

   git clone git@github.com:your_github_name/your_repo_name.git
   cd your_repo_name


**Encryption**

.. code-block:: text

   travis login --pro --github-token token
   travis encrypt --pro VARNAME="token"
   
   
*converts* "VARNAME=\"token\"" *into* ".... encrypted data ...."

Deploy
------
In the .travis.yml :

**Add token:**

.. code-block:: text

   env:
     global:
      secure: ".... encrypted data ...."
      
*converts* ".... encrypted data ...." *into* VARNAME="token"

You can now access you token by calling the variable VARNAME
(on windows with shell : $VARNAME)

**Add before deploy:**

.. code-block:: text

   before_deploy:
   - git config --local user.name "name"  #<- change to your github name
   - git config --local user.email "email"  #<- change to your github email

**Add deploy:**

.. code-block:: text

   deploy:
     provider: releases
     api_key: "$VARNAME" #<- change to your VARNAME
     file_glob: true
     file:
     - executables/**.exe
     - executables/**.jar
     - executables/**.sha256
     skip_cleanup: true
     on:
       tags: true
       repo: owner/repoName #<- change to your repo name
       branch: branchName #<- change to your branch name

Links
=====
.. [1] Official documentation on using TravisCI on Github https://docs.travis-ci.com/user/tutorial/

.. [2] Official documentation Github access token https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token

.. [3] Official documentation on keys encryption https://docs.travis-ci.com/user/encryption-keys/
