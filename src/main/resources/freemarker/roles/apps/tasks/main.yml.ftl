---
- apt: pkg="{{ item }}" state=latest update_cache=yes cache_valid_time=3600
  with_items:
    - git
  become: yes
  become_method: sudo