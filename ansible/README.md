# Ivana Chess - Ansible

Ansible playbooks to install Ivana Chess API and/or webapp.

## How to use

**The only OS supported is Debian 10.**

Create a file `inventory.yml` with two groups:

- `ivana_api` which contains servers on which to install the API;
- `ivana_webapp` which contains servers on which to install the webapp.

Then run:

```bash
ansible-playbook -i inventory.yml playbooks/ivana-chess.yml -e '@vars/default.yml' --ask-become-pass
```
