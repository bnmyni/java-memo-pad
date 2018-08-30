/*******************************************************************************
 * Copyright (c) 2013.  Pyrlong All rights reserved.
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

package com.tuoming.mes.collect.dpp.file.handles;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.CharacterSetToolkit;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.file.TextLine;
import com.tuoming.mes.collect.dpp.models.TextItemRule;

@Scope("prototype")
@Component("FormatedTextLineHandle")
public class FormatedTextLineHandle extends AbstractTextLineHandle {

	private static Logger logger = LogFacade.getLog4j(FormatedTextLineHandle.class);
	private Map<String, Integer> columnFormatMap = new LinkedHashMap<String, Integer>();
	private Map<String, String> rawMap = new LinkedHashMap<String, String>();
	private boolean lastRowIsValue = false;
	private boolean columnFormatMapUpdated = false;

	private void updateColumnFormatMap(TextLine line) throws Exception {
		columnFormatMap = new LinkedHashMap<String, Integer>();
		char[] chars = line.toString().toCharArray();
		int idx = 0;
		boolean rowNeedRaise = false;
		StringBuilder sb = new StringBuilder();
		int lastStartIdx = 0;
		boolean notSplit = true;
		int cidx = -1;
		for (idx = 0; idx < chars.length; idx++) {
			cidx++;
			char c = chars[idx];
			notSplit = true;
			if (StringUtil.isEmpty(super.logParser.getSplitRegex())) {
				if (c == '\t') // 如果遇到一个tab字符则认为遇到分割符号
					notSplit = false;
				// 如果发现连续两个空格则认为是分割符号
				if (c == ' ' && idx + 1 < chars.length && chars[idx + 1] == ' ') {
					notSplit = false;
					idx++;
					cidx++;
				}
			} else if (super.logParser.getSplitRegex().charAt(0) == c) {
				notSplit = false;
			}
			// 如果没有遇到分隔字符
			if (notSplit) {
				if (sb.length() == 0) {
					lastStartIdx = cidx;
					// sb = new StringBuilder();
				}
				// 如果是中文 则数据索引再加一
				if (logParser.isHaveUnicode() && CharacterSetToolkit.checkType(c).isWideChar()) {
					cidx = cidx + 1;
				}
				sb.append(c);
			} else {
				if (sb.length() == 0)
					continue;
				if (StringUtil.isNotEmpty(super.logParser.getSplitRegex())) {
					lastStartIdx -= 1;
				}
				String col = sb.toString().trim();
				col = col.replace(" ", "");
				sb.setLength(0);// = null;
				if (columnFormatMap.size() == 0 && cidx > 0) {
					// 如果是第一列 并且 lastStartIdx >0 则将 lastStartIdx 设置为0
					columnFormatMap.put(col, 0);
				} else {
					columnFormatMap.put(col, lastStartIdx);
				}
				if (_itemAdded.containsKey(col)) {
					// 如果當前標識重複出現，則表示需要生成數據行
					newItemAddedMap();
					rowNeedRaise = true;
				}
				_itemAdded.put(col, null);
			}
		}
		if (sb != null) {
			if (StringUtil.isNotEmpty(super.logParser.getSplitRegex())) {
				lastStartIdx -= 1;
			}
			String col = sb.toString().trim();
			if (StringUtil.isNotEmpty(col)) {
				columnFormatMap.put(col, lastStartIdx);
			}

			if (_itemAdded.containsKey(col)) {
				// 如果當前標識重複出現，則表示需要生成數據行
				newItemAddedMap();
				rowNeedRaise = true;
			}
			_itemAdded.put(col, null);
		}
		if (rowNeedRaise) {
			if (lastRowIsValue)
				raiseRow(this.getHeaderRowRegex().size() > 1);
		} else {
			lastRowIsValue = false;
		}
		columnFormatMapUpdated = true;
	}

	private void raiseRow(boolean force) throws Exception {
		// 生成新的数据行
		try {
			if (!lastRowIsValue && !force)
				return;
			newRow();
			synchronized (rawMap) {
				rawMap.putAll(envs);
			}
			// 设置默认值
			for (TextItemRule rule : logParser.getItemRuleList()) {
				if (StringUtil.isNotBlank(rule.getValueExpression())) {
					Object value = DSLUtil.getDefaultInstance().buildString(rule.getValueExpression(), rawMap);
					// if (rule.getValueExpression().equals(value) ||
					// StringUtil.isEmpty(value.toString()))
					if (rule.getValueExpression().equals(value))
						value = rule.getDefaultValue();
					// if (!rule.getCachedItem())
					// rule.setDefaultValue("");
					if (isValid(value)) {
						CurrentRow.setValue(rule.getName(), value.toString().trim());
						if (rule.getCachedItem()) {
							rule.setDefaultValue(value + "");
						}
					}
				}
			}
			if (dataRowHandler != null)
				dataRowHandler.process("", CurrentRow);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		rawMap = new LinkedHashMap<String, String>();
		lastRowIsValue = false;
	}

	/**
	 * 更新原始數據行信息
	 *
	 * @param line
	 * @throws Exception
	 */
	private void updateRawMapValue(TextLine line) throws Exception {
		if (lastRowIsValue) {
			raiseRow(false);
		}
		String tempLine = line.toString();
		List<Integer> idxList = new ArrayList<Integer>();
		List<String> colNameList = new ArrayList<String>();
		for (Entry<String, Integer> entry : columnFormatMap.entrySet()) {
			idxList.add(entry.getValue());
			colNameList.add(entry.getKey());
		}
		for (int i = 0; i < idxList.size(); i++) {
			int start = idxList.get(i);
			int end = StringUtil.getLength(tempLine);
			if (i + 1 < idxList.size()) {
				end = idxList.get(i + 1) > end ? end : idxList.get(i + 1);
			}

			String value = logParser.isHaveUnicode() ? StringUtil.newSubstring(tempLine, start, end) : StringUtil.substring(tempLine, start, end);

			// TODO 处理列数据没有对齐的情况，中兴设备升级后出现了这种情况
			rawMap.put(colNameList.get(i), value.trim());
			logger.debug(colNameList.get(i) + "->" + value + "(" + start + "," + end + ")");
		}
		lastRowIsValue = true;
	}

	@Override
	public synchronized void processLine(TextLine line, Map<String, String> envs) {
		super.processLine(line, envs);
		for (String regx : getHeaderRowRegex()) {
			if (line.isMatch(regx)) {
				try {
					updateColumnFormatMap(line);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				return;
			}
		}
		for (String regx : getIgnoreRegex()) {
			if (line.isMatch(regx)) {
				if (lastRowIsValue || columnFormatMapUpdated)
					try {
						if (lastRowIsValue)
							raiseRow(this.getHeaderRowRegex().size() > 1);
						newItemAddedMap();
						lastRowIsValue = false;
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				columnFormatMapUpdated = false;
				return;
			}
		}
		// 如果一行数据不是標識行，並且非空，則按照數據行處理
		if (!StringUtil.isEmpty(line.toString().trim()) && columnFormatMapUpdated) {
			try {
				// 如果没有设置数据行标识 或者 能与数据行标识匹配上
				if (StringUtil.isEmpty(logParser.getValueLineRegex()) || line.isMatch(logParser.getValueLineRegex())) {
					updateRawMapValue(line);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		// 否则查看是否需要正则解析 ，此处临时做如下约定：即正则解析结果均是需要缓存的数据
		for (TextItemRule rule : logParser.getItemRuleList()) {
			if (StringUtil.isNotEmpty(rule.getRegexFilter())) {
				if (line.isMatch(rule.getRegexFilter())) {
					Object result = DSLUtil.getDefaultInstance().compute(rule.getValueExpression(), envs);
					logger.debug(rule.getName() + "->" + result);
					rule.setDefaultValue(result + "");
				}
			}
		}
	}

	@Override
	public void done() {
		if (lastRowIsValue || columnFormatMapUpdated)
			try {
				raiseRow(false);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		this.lastRowIsValue = false;
		this.columnFormatMapUpdated = false;
		_itemAdded = new LinkedHashMap<String, String>();
	}
}
