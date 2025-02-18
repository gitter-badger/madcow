/*
 * Copyright 2012 4impact, Brisbane, Australia
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package au.com.ps4impact.madcow.runner.webdriver.blade.table.util

/**
 * 
 *
 * @author: Gavin Bunney
 */
class Column {

    protected String tableXPath

    protected def columnHeader

    public Column(String tableXPath, def columnHeader) {
        this.tableXPath = tableXPath
        this.columnHeader = columnHeader
    }

    public String getColumnPositionXPath() {
        if (columnHeader == "firstColumn")
            return "1"
        else if (columnHeader == "lastColumn")
            return "count(${tableXPath}/thead/tr/th[position() = last()]/preceding-sibling::*)+1"
        else if (columnHeader.toString().toLowerCase() ==~ /column\d*/)
            return columnHeader.toString().substring(6)
        else
            return "count((" +
                        "${tableXPath}/thead/tr/th[normalize-space(.//text()) = '${columnHeader}' or normalize-space(.//@value) = '${columnHeader}'] |" +
                        "${tableXPath}/tbody/tr/td[normalize-space(.//text()) = '${columnHeader}' or normalize-space(.//@id) = '${columnHeader}' or normalize-space(.//@name) = '${columnHeader}']" +
                        ")/preceding-sibling::*)+1"
    }
}
