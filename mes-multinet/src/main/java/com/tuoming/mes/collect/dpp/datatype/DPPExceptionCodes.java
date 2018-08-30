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

import com.pyrlong.exception.ExceptionCodes;

/**
 * Created by James on 14-1-14.
 */
public class DPPExceptionCodes extends ExceptionCodes {
    //需要写入缓存的DataRow对象不能为空，并且其所属DataTable对象也不能为空!
    public final static String ERROR_DPP_2001 = "DPP-2001";
    //用于初始化缓存的DataTable对象不能为null
    public final static String ERROR_DPP_2002 = "DPP-2002";
    public final static String ERROR_DPP_2003 = "DPP-2003";
    public final static String ERROR_DPP_2004 = "DPP-2004";
    public final static String ERROR_DPP_2005 = "DPP-2005";
    public final static String ERROR_DPP_2006 = "DPP-2006";
    public final static String ERROR_DPP_2007 = "DPP-2007";
    public final static String ERROR_DPP_2008 = "DPP-2008";
    public final static String ERROR_DPP_2009 = "DPP-2009";

}
