#!/bin/python3
#
# This script is used to generate markdown table for available configuration properties.
# It must be executed from the repository root directory.
#
# Usage: ./utils/generate-markdown-conf-table.py <appliation.yml filepaths ...>

import re
import sys
from typing import TextIO, Dict, Optional, List

YAML_COMMENT_PATTERN = re.compile("^ *# (.*)$")
YAML_PROPERTY_PATTERN = re.compile("^( *)([A-z-]+):(.*)$")
PROPERTY_VALUE_PATTERN = re.compile("\\${([A-Z_]+)(:(.*)+)?}")


class Property:
    name: str
    comment: str
    env_var_name: Optional[str]
    default: Optional[str]

    def __init__(self, name: str, comment: str, env_var_name: Optional[str] = None, default: Optional[str] = None):
        self.name = name
        self.comment = comment
        self.env_var_name = env_var_name
        self.default = default


class RowEntry:
    value: Optional[str]
    length: int

    def __init__(self, value: Optional[str], length: int):
        self.value = value
        self.length = length


def _complete_property_name(property_name: str, parents: List[str]) -> str:
    prefix = '.'.join(parents)
    if prefix == '':
        return property_name
    else:
        return prefix + '.' + property_name


def _display_table(properties: List[Property]):
    property_header = "Property"
    env_var_header = "Environment variable"
    desc_header = "Description"
    default_header = "Default value"
    headers = {
        property_header: len(max([prop.name for prop in properties] + [property_header], key=len)),
        env_var_header: len(
            max([prop.env_var_name for prop in properties if prop.env_var_name is not None] + [env_var_header], key=len)
        ),
        desc_header: len(max([prop.comment for prop in properties] + [desc_header], key=len)),
        default_header: len(
            max([prop.default for prop in properties if prop.default is not None] + [default_header], key=len)
        )
    }
    columns_len = [length for header, length in headers.items()]
    _table_headers(headers)
    for prop in properties:
        _table_row(
            [
                RowEntry(prop.name, columns_len[0]),
                RowEntry(prop.env_var_name, columns_len[1]),
                RowEntry(prop.comment, columns_len[2]),
                RowEntry(prop.default, columns_len[3])
            ]
        )


def _center(value: str, max_len: int):
    blank_nb = max_len - len(value)
    if blank_nb % 2 == 0:
        left_blank = _repeat_string(" ", int(blank_nb / 2))
        right_blank = left_blank
    else:
        left_blank = _repeat_string(" ", int(blank_nb / 2 + 1))
        right_blank = _repeat_string(" ", int(blank_nb / 2))
    return left_blank + value + right_blank


def _repeat_string(string: str, nb: int) -> str:
    blank_str = ''
    for i in range(0, nb):
        blank_str += string
    return blank_str


def _table_headers(headers: Dict[str, int]):
    dash_string = "|:"
    for header, length in headers.items():
        dash_string += _repeat_string("-", length) + ":|:"
    headers_entries = [RowEntry(value, length) for value, length in headers.items()]
    _table_row(headers_entries)
    print(dash_string[:-1])


def _table_row(row: List[RowEntry]):
    row_string = "| "
    for entry in row:
        if entry.value is None:
            value = '-'
        else:
            value = entry.value
        row_string += _center(value, entry.length) + " | "
    print(row_string[:-1])


def _read_yaml(file: TextIO) -> List[Property]:
    parents: List[str] = []
    last_comment: Optional[str] = None
    properties: List[Property] = []
    for line in file.readlines():
        if len(parents) == 0 or parents[0] == 'ivana-chess':
            comment_matcher = YAML_COMMENT_PATTERN.match(line)
            if comment_matcher is not None:
                last_comment = comment_matcher.group(1)
            else:
                property_matcher = YAML_PROPERTY_PATTERN.match(line)
                if property_matcher is not None:
                    indentation = property_matcher.group(1)
                    property_name = property_matcher.group(2)
                    property_value = property_matcher.group(3).strip()
                    diff = int(len(parents) - len(indentation) / 2)
                    if diff > 0:
                        parents = parents[0:(len(parents) - diff)]
                    if property_value == '':
                        parents.append(property_name)
                    else:
                        complete_property_name = _complete_property_name(property_name, parents)
                        property_value_matcher = PROPERTY_VALUE_PATTERN.match(property_value)
                        if property_value_matcher is None:
                            print('WARN: ' + complete_property_name + ' is invalid', file=sys.stderr)
                        elif last_comment is None:
                            print('WARN: ' + complete_property_name + ' has no comment', file=sys.stderr)
                        else:
                            env_var_name = property_value_matcher.group(1)
                            default_value = property_value_matcher.group(2)
                            if default_value is not None:
                                default_value = default_value[1:]
                            properties.append(
                                Property(complete_property_name, last_comment, env_var_name, default_value)
                            )
    return properties


def main():
    for application_yaml_filepath in sys.argv[1:]:
        print(application_yaml_filepath)
        with open(application_yaml_filepath) as application_yaml_file:
            properties = _read_yaml(application_yaml_file)
            _display_table(properties)
        print()


if __name__ == '__main__':
    main()
