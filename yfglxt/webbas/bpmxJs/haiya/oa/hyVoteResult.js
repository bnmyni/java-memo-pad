
/**
 * 查看投票结果
 * @param voteId
 */
function showResult(voteId){
	var url	= __ctx+"/produce/oa/hyVoteResult/calResult.ht?voteId="+voteId;
	url=url.getNewUrl();
	DialogUtil.open({
		url:url,
		title:"查看投票结果",
		height:'600',
		width:'900'
	});
}

/**
 * 查看投票结果明细
 * @param voteObjId
 */
function showResultDetail(voteObjId){
	var url	= __ctx+"/produce/oa/hyVoteResult/list.ht?voteObjId="+voteObjId;
	url=url.getNewUrl();
	DialogUtil.open({
		url:url,
		title:"查看明细",
		height:'600',
		width:'900'
	});
}