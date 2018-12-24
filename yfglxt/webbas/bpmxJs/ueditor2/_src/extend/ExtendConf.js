/**
 * ueditor扩展插件的配置
 */
var ExtendConf = UE.ExtendConf = {
	iframeUrlMap: {
		'input': '~/dialogs/extend/input/input.jsp',
		'opinion': '~/dialogs/extend/opinion/opinion.jsp',
		'insertfunction': '~/dialogs/extend/insertfunction/MathExpEditor.jsp',
		'importform': '~/dialogs/extend/import/importform.jsp',
		'customdialog': '~/dialogs/extend/dialog/dialog.jsp',
		'customquery':'~/dialogs/extend/query/query.jsp',
		'customquerydialog':'~/dialogs/extend/query/dialog.jsp',
		'cascadequery':'~/dialogs/extend/query/queryMulVar.jsp',
		'numbervalidate':'~/dialogs/extend/validate/number.jsp',
		'daterangevalidate':'~/dialogs/extend/validate/daterange.jsp',
		'international':'~/dialogs/extend/international/international.jsp',
		'wordtemplate':'~/dialogs/extend/template/wordtemplate.jsp',
		'datecalculate': '~/dialogs/extend/insertfunction/datecalculate.jsp',
		'exportexceldialog': '~/dialogs/extend/exceltemp/exportexceldialog.jsp',
		'importexceldialog': '~/dialogs/extend/exceltemp/importexceldialog.jsp'
	},
		
	btnCmds:['tableformat','choosetemplate','opinion','input','taskopinion','flowchart',
	         'insertfunction','cutsubtable','pastesubtable','customdialog','clearcell',
	         'insertrownext','insertcolnext','customquery','uncustomquery','numbervalidate','daterangevalidate','datecalculate','international','serialnum',
	         'hidedomain','textinput','textarea','checkbox','radioinput','selectinput',
             'dictionary','personpicker','departmentpicker','datepicker','officecontrol','subtable',
             'ckeditor','rolepicker','positionpicker','attachement','importform','exportform','pasteinput','cascadequery','wordtemplate','exportexceldialog','importexceldialog'],

	dialogBtns: ['importform', 'customquery','customquerydialog','cascadequery','opinion','customdialog','input','insertfunction','numbervalidate','daterangevalidate','datecalculate','international','uncascadequery','setreadonly','delreadonly','wordtemplate','exportexceldialog','importexceldialog']
}