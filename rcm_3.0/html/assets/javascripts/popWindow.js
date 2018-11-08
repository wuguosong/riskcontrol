// 注：该弹窗插件依赖jQuery和bootstrap，使用时务必导入jQuery插件和bootstrap.js和bootstrap.css
(function($) {
	// alert/confirm/prompt html
	var html = '<div id="[Id]" class="modal fade" role="dialog" aria-labelledby="modalLabel"> 		'
			+ '	<div class="modal-dialog modal-sm">                                        	  				'
			+ '		<div class="modal-content">                                            			'
			+ '			<div class="modal-header">                                         			'
			+ '				<button type="button" class="close" data-dismiss="modal">      			'
			+ '					<span aria-hidden="true">&times;</span>                    			'
			+ '				</button>                                                      			'
			+ '				<h4 class="modal-title" id="modalLabel">[Title]</h4>           			'
			+ '			</div>                                                             			'
			+ '			<div class="modal-body">                                           			'
			+ '				<span class="text">[Message]</span>                                     '
			+ '				<input class="form-control input-lg promptInput" type="text"/>   		'
			+ '			</div>                                                             			'
			+ '			<div class="modal-footer">                                         			'
			+ '				<button type="button" class="btn btn-default cancel"           			'
			+ '					data-dismiss="modal">[btnCancel]</button>                  			'
			+ '				<button type="button" class="btn btn-primary ok">[btnOk]</button>                      			'
			+ '			</div>                                                             			'
			+ '		</div>                                                                 			'
			+ '	</div>                                                                     			'
			+ '</div>                                                                        		';
	//
	var popHtml = '<div id="[Id]" class="modal fade" role="dialog" aria-labelledby="modalLabel"> 		'
			+ '	<div class="modal-dialog">                                        	  				'
			+ '		<div class="modal-content">                                            			'
			+ '			<div class="modal-header">                                         			'
			+ '				<button type="button" class="close" data-dismiss="modal">      			'
			+ '					<span aria-hidden="true">&times;</span>                    			'
			+ '				</button>                                                      			'
			+ '				<h4 class="modal-title" id="modalLabel">[Title]</h4>           			'
			+ '			</div>                                                             			'
			+ '			<div class="modal-body">                                           			'
			+ '				<div class="drop-menu">																'
			+ '						<div class="search-input has-feedback">								'
			+ '							<input type="text" placeholder="请输入负责人姓名" class="in-t" name="">				'
			+ '							<i class="fa fa-search form-control-feedback"></i>			'
			+ '						</div>																					'
			+ '						<ul class="dropdown-list">													'
			+ '							<li class="mm-dropdown">																	'
			+ '								<a href="javascript:;" title=""><span class="text" title="北控水务集团">北控水务集团</span><i class="ico fa fa-plus"></i></a>		'
			+ '								<ul class="sub-list">																																												'
			+ '									<li><a href="#" title="" tabindex="-1">三级菜单</a></li>																												'
			+ '									<li><a href="#" title="" tabindex="-1">三级菜单</a></li>																												'
			+ '									<li><a href="#" title="" tabindex="-1">三级菜单</a></li>																												'
			+ '									<li><a href="#" title="" tabindex="-1">三级菜单</a></li>																												'
			+ '									<li><a href="#" title="" tabindex="-1">三级菜单</a></li>																												'
			+ '								</ul>																																																	'
			+ '							</li>																																																			'
			+ '							<li class="mm-dropdown">																																																			'
			+ '							<a href="javascript:;" title=""><span class="text">一级事业部</span><i class="ico fa fa-plus"></i></a>												'
			+ '								<ul class="sub-list">																																												'
			+ '									<li><a href="#" title="" tabindex="-1">二级事业部</a></li>																											'
			+ '									<li><a href="#" title="" tabindex="-1">二级事业部</a></li>																											'
			+ '									<li><a href="#" title="" tabindex="-1">二级事业部</a></li>																											'
			+ '									<li><a href="#" title="" tabindex="-1">二级事业部</a></li>																											'
			+ '									<li><a href="#" title="" tabindex="-1">二级事业部</a></li>																											'
			+ '								</ul>																																																	'
			+ '							</li>			'
			+ '							<li><a href="javascript:;" title=""><span class="text">XX事业部</span></a></li>		'
			+ '							<li><a href="javascript:;" title=""><span class="text">XX事业部</span></a></li>		'
			+ '							<li><a href="javascript:;" title=""><span class="text">XX事业部</span></a></li>		'
			+ '						</ul>		'
			+ '					</div>                                    '
			+ '			</div>                                                             			'
			+ '			<div class="modal-footer">                                         			'
			+ '				<button type="button" class="btn btn-default cancel"           			'
			+ '					data-dismiss="modal">[btnCancel]</button>                  			'
			+ '				<button type="button" class="btn btn-primary ok">[btnOk]</button>                      			'
			+ '			</div>                                                             			'
			+ '		</div>                                                                 			'
			+ '	</div>                                                                     			'
			+ '</div>																					';
	// dialog html
	var dialogHtml = '<div id="[Id]" class="modal fade" role="dialog" aria-labelledby="modalLabel"> '
			+ '	<div class="modal-dialog">                                        	  				'
			+ '		<div class="modal-content">                                            			'
			+ '			<div class="modal-header">                                         			'
			+ '				<button type="button" class="close" data-dismiss="modal">      			'
			+ '					<span aria-hidden="true">&times;</span>                    			'
			+ '				</button>                                                      			'
			+ '				<h4 class="modal-title" id="modalLabel">[Title]</h4>           			'
			+ '			</div>                                                             			'
			+ '			<div class="modal-body">             										'
			+ '			</div>                                                             			'
			+ '		</div>                                                                 			'
			+ '	</div>                                                                     			'
			+ '</div>                                                                        		';
	// 初始化
	$.initPop = function(options) {
		options = $.extend({}, {
			title : "操作提示",
			message : "提示内容",
			btnOk : "确定",
			btnCancel : "取消"
		}, options || {});
		var modalId = "modalId" + new Date().valueOf();
		// 替换[]的相关变量
		var reg = new RegExp("\\[([^\\[\\]]*?)\\]", 'igm');
		var content = html.replace(reg, function(node, key) {
			return {
				Id : modalId,
				Title : options.title,
				Message : options.message,
				btnOk : options.btnOk,
				btnCancel : options.btnCancel
			}[key];
		});
		$('body').append(content);
		$('#' + modalId).modal({
			backdrop : 'static'
		});
		$('#' + modalId).on("hide.bs.modal", function(e) {
			$('body').find('#' + modalId).remove();
			$('body').removeClass('modal-open');
		});
		return modalId;
	};

	// 扩展jQuery全局函数
	$.extend({
		returnValue : "",// 返回值
		modalAry : new Array(0),// modal队列
		modalCallbackAry : new Array(0),// callback队列
		modalClosebackAry : new Array(0),// closeback队列
		modalCloseMode : null, // 关闭方式

		// 关闭dialog
		closeDialog : function() {
			$.modalCloseMode = "in";
			$.modalAry[$.modalAry.length - 1].modal('hide');
		},

		// alert弹窗
		alert : function(options) {
			if (typeof (options) == 'string') {
				options = {
					message : options
				};
			}
			options = $.extend({}, {
				title : "温馨提示"
			}, options || {});
			var modalId = $.initPop(options);
			var $modal = $('#' + modalId);
			$modal.find('.ok').addClass('btn-primary');
			$modal.find('.cancel').hide();
			$modal.find('.promptInput').hide();
			$modal.find('.ok').click(function() {
				$modal.modal("hide");
			});
		},

		// confirm弹窗
		confirm : function(options, callback) {
			if (typeof (options) == 'string') {
				options = {
					message : options
				};
			}
			options = $.extend({}, {
				title : "确认信息"
			}, options || {});
			if (callback && callback instanceof Function) {
				options = $.extend({}, {
					callback : callback
				}, options);
			}
			var modalId = $.initPop(options);
			var $modal = $('#' + modalId);
			$modal.find('.ok').addClass('btn-success');
			$modal.find('.promptInput').hide();
			$modal.find('.ok').click(function() {
				if (options.callback) {
					options.callback();
				}
				$modal.modal("hide");
			});
		},

		// prompt弹窗
		prompt : function(options, callback) {
			if (typeof (options) == "string") {
				options = {
					defaultVal : options
				};
			}
			if (options instanceof Function) {
				options = $.extend({}, {
					callback : options
				}, options);
			}
			if (callback instanceof Function) {
				options = $.extend({}, {
					callback : callback
				}, options || {});
			}
			options = $.extend({}, {
				title : "输入信息"
			}, options || {});
			var modalId = $.initPop(options);
			var $modal = $('#' + modalId);
			$modal.find('.ok').addClass('btn-success');
			$modal.find('.messageTip').hide();
			// 输入框
			var $input = $modal.find('.promptInput');
			$input.val(options.defaultVal);
			// 限制字数
			var length = (options.length && parseInt(options.length)) || 0;
			// 点击ok按钮
			$modal.find('.ok').click(function(e) {
				var inputVal = $input.val();
				// 是否超过字数限制
				if (length > 0 && inputVal.length > length) {
					$input.css("border-color", "red");
					$modal.attr("title", "字符个数不能超过" + length);
				} else {
					if (options.callback) {
						options.callback(inputVal);
					}
					$modal.modal("hide");
				}
			});
			// 输入框键盘监听
			$input.keyup(function(e) {
				if (e.keyCode == "13") {
					$modal.find('.ok').click();
				} else {
					if (length > 0 && $input.val().length > length) {
						$input.css("border-color", "red");
						$modal.attr("title", "字符个数不能超过" + length);
					} else {
						$input.css("border-color", "#ccc");
						$modal.attr("title", "");
					}
				}
			});
		},

		// dialog弹窗
		dialog : function(options) {
			if (typeof (options) == "string") {
				options = {
					url : options
				};
			}
			options = $.extend({}, {
				title : '网页信息'
			}, options || {});
			var modalId = "modalId" + new Date().valueOf();
			var reg = new RegExp("\\[([^\\[\\]]*?)\\]", 'igm');
			var content = dialogHtml.replace(reg, function(node, key) {
				return {
					Id : modalId,
					Title : options.title
				}[key];
			});
			$('body').append(content);
			var $modal = $('#' + modalId);
			$modal.find('.modal-body').load(options.url);
			$modal.find('.modal-body').css("maxHeight", 525);
			$modal.find('.modal-body').css("overflow", "auto");
			$modal.modal({
				backdrop : 'static'
			});
			$modal.on("hidden.bs.modal", function() {
				$(this).remove();
				// 从队列中移除对应的弹窗数据
				$.modalAry.pop();
				var callback = $.modalCallbackAry.pop();
				var closeback = $.modalClosebackAry.pop();
				if ($.modalCloseMode) {
					if (callback instanceof Function) {
						callback($.returnValue);
					}
				} else {
					if (closeback instanceof Function) {
						closeback($.returnValue);
					}
				}
				$.modalCloseMode = null;
			});
			// modal放入队列
			$.modalAry.push($modal);
			// callback放入队列
			$.modalCallbackAry.push(options.callback);
			// closeback放入队列
			$.modalClosebackAry.push(options.closeback);
		}
	});
})(jQuery);