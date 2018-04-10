---
- apt: pkg="{{ item }}" state=latest update_cache=yes cache_valid_time=3600
  with_items:
    - git
    - zip
    - unzip
    - xz-utils
    - python-pip

- pip: name="{{ item }}" umask=0022
  with_items:
    - docker-compose