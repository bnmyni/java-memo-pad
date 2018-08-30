/*******************************************************************************
 * Copyright (c) 2014.  Pyrlong All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tuoming.mes.collect.dpp.datatype;

/**
 * Created by James on 14-1-14.
 */
public class DPPConstants {

    public final static String CACHE_ROOT = "dpp.cache_root";
    //缓存相关配置
    public final static String BDB_LOG_BUFFER_SIZE = "dpp.bdb_log_buffer_size";
    public final static String BDB_LOG_CACHE_SIZE = "dpp.bdb_log_cache_size";
    public final static String BDB_SCHEME_FILE_EXTENSION = ".table";
    public final static String BDB_CACHE_FILE_EXTENSION = ".cache";
    public final static String BDB_CATALOG_FILE_EXTENSION = ".catalog";
    public final static String BDB_DB_NAME_SUFFIX = "_class_catalog";
    public final static String BDB_SECOND_DB_NAME_SUFFIX = ".bdb";
    //
    public final static String DB_CONNECTION_SECTION_NAME = "connectionStrings";
    public final static String DB_DEFAULT_NAME = "MainDB";
    //文件解析工具类常量定义
    public final static String FILE_REGEX_SPLIT = "#";
    public final static String FILE_PRINT_HEADER = "dpp.file_print_header";
    public final static String FILE_PARSED_EXTENSION = ".done";
    public final static String FILE_CURRENT_LINE = "CurrentLine";
    public final static String CSV_FILE_SPLIT_CHAR = "dpp.csv_split_char";
    public final static String CSV_FILE_ENCODING = "dpp.csv_encoding";
    public final static String LOAD_DATA_ENCLOSED = "dpp.enclosed";
    public final static String CSV_FILE_NULL_VALUE = "dpp.csv_null_value";

    public final static String FTP_CSV_SKIP_LINE = "dpp.ftp_csv_skip_line";
    //数据库操作相关常量
    public final static String DB_QUERY_TIME_OUT = "dpp.query_time_out";
    public final static String DB_CONNECTION_CHECK = "dpp.conn_check_time";
    public final static String DB_EXECUTE_FILE_BREAK_ON_ERROR = "dpp.execute_break_on_error";
}
