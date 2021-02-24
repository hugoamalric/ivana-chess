# Ivana Chess - Ansible

Ansible playbooks to install Ivana Chess API and/or webapp.

## How to use

**The only OS supported is Debian 10.**

Create a file `inventory.yml` with groups:

- `ivana_chess_api_db` which contains the server on which to install the database;
- `ivana_chess_api` which contains servers on which to install the API;
- `ivana_chess_webapp` which contains servers on which to install the webapp.

Then run:

```bash
ansible-playbook -i inventory.yml playbooks/ivana-chess.yml --ask-become-pass
```
