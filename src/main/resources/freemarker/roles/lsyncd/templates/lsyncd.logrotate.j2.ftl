#jinja2: trim_blocks: True, lstrip_blocks: True
-- This file was generated by Ansible. Do not modify directly!

{{ lsyncd.logDir | mandatory }}/{{ lsyncd.logFile | mandatory }} {
    missingok
    notifempty
    sharedscripts
    postrotate
    if [ -f /var/lock/lsyncd ]; then
      /usr/sbin/service lsyncd restart > /dev/null 2>/dev/null || true
    fi
    endscript
}

{{ lsyncd.logDir | mandatory }}/{{ lsyncd.statusFile | mandatory }} {
    missingok
    notifempty
    sharedscripts
    postrotate
    if [ -f /var/lock/lsyncd ]; then
      /usr/sbin/service lsyncd restart > /dev/null 2>/dev/null || true
    fi
    endscript
}