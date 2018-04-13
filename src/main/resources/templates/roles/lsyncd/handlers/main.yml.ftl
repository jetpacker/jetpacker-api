---
- name: restart lsyncd
  service:
    name: lsyncd
    state: restarted
  become: true
  become_method: sudo
  listen:
    - restart_lsyncd