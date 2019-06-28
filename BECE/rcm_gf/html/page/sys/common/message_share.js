/**
 * Created by Administrator on 2019/4/8 0008.
 */
ctmApp.register.controller('shareMessageCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter','$window',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter, $window) {
        $scope.message_id_decode = $filter('decodeURI')($routeParams.id);
        $scope.message = {};
        $scope._message = {};
        $scope._message.originalId = 0;
        $scope._message.parentId = 0;
        $scope._message.procInstId = 0;
        $scope._message.repliedBy = '';
        $scope._message.repliedName = '';
        $scope._messages_array_ = [];
        $scope._query_messages_list_ = function (procInstId, parentId) {
            $http({
                method: 'post',
                url: srvUrl + 'message/queryMessagesList.do',
                data: $.param({
                    'procInstId': procInstId,
                    'parentId': parentId
                }),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data) {
                $scope._messages_array_ = data;
            });
        };
        // 用户的其他操作,预留方法
        $scope._user_about_click_ = function(_source_, _user_id_, _user_name_){
            console.log("查看用户：" + _source_ + " " + _user_id_ + " " + _user_name_);
        };
        // 检测滑动
        $scope._check_collapse_event_ = function(_obj_, _idx_){
            var _class = $('#_message_panel_body_' + _obj_.messageId).attr("class");
            var _span = '';
            if("panel-collapse collapse in" == _class){
                _span += _obj_['createdName'] + '&nbsp;';
                _span += _obj_['messageDate'] + '&nbsp;';
                var _c = htmlToText(_obj_.messageContent);
                if(!isEmpty(_c)){
                    if(_c.length >= 18){
                        _span += _c.substr(0, 19) + '......';
                    }else{
                        _span += _c;
                    }
                }
            }
            $('#_span_hide_content_' + _idx_).html(_span);
        };
        $scope.getParentMessageInfo = function(){
            var _l = $scope._messages_array_.length;
            for(var _i = 0; _i < _l; _i ++){
                var _subArray = $scope._messages_array_[_i];
                var _subL = _subArray.length;
                for(var _j = 0; _j < _subL; _j++){
                    var _m = _subArray[_j];
                    if(_m.messageId == $scope.message_id_decode){
                        return _m;
                    }
                }
            }
            return null;
        };
        $scope.initMessage = function(messageId){
            $http({
                method: 'post',
                url: srvUrl + 'message/get.do',
                data: $.param({
                    'messageId': messageId
                }),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data) {
                $scope.message = data['result_data'];
                if(!isEmpty($scope.message)){
                    $scope._message.procInstId = $scope.message.procInstId;
                    $scope._query_messages_list_($scope.message.procInstId, 0);
                    $scope._private_setTips($scope.message_id_decode);
                }
            });
        };
        $scope._clear_message_from = function(){
            $scope._message.originalId = '';
            $scope._message.parentId = '';
            $scope._message.repliedBy = '';
            $scope._message.repliedName = '';
            $scope._message.messageContent = '';
        };
        $scope._submit_message_form_ = function (_original_id_, _parent_id_, _replied_by_, _replied_name_, _idx_) {
            var formData = {};
            formData = $scope._message;
            formData.originalId = _original_id_;
            formData.parentId = _parent_id_;
            formData.repliedBy = _replied_by_;
            formData.repliedName = _replied_name_;
            formData.messageContent = $('#_message_textarea_bottom_' + _idx_).html();
            if (isEmpty(htmlToText(formData.messageContent))) {
                $.alert('回复内容不能为空!');
                return;
            }
            if(_common_get_string_byte_length(formData.messageContent) > 2500){
                //$.alert('内容不能超过2500个字符!');
                //return;
            }
            var pm = $scope.getParentMessageInfo();
            if(!isEmpty(pm)){
                formData.messageType = pm.messageType;
            }
            $http({
                method: 'post',
                url: srvUrl + 'message/add.do',
                data: $.param(formData),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data) {
                $scope.initMessage($scope.message_id_decode);
                $scope._clear_message_from();
                $.alert('回复留言成功!');
            });
        };
        $scope.initMessage($scope.message_id_decode);
        $scope._init_uuid_global_ = $scope.credentials.UUID;
        $scope._is_show_reply_btn_ = true;
        // 留言中的过程附件
        $scope._message_upload_file_ = function(_file_, _message_){
            Upload.upload({
                url: srvUrl + 'cloud/upload.do',
                data: {
                    file: _file_,
                    'docType':'sys_message_' + _message_.messageType,
                    'docCode':_message_.procInstId,
                    'pageLocation':_message_.messageId
                }
            }).then(function (_resp_) {
                var _result = _resp_.data;
                if(!isEmpty(_result)){
                    if(_result.success){
                        var _cloud_file_dto_ = _result['result_data'];
                        if(!isEmpty(_cloud_file_dto_)){
                            _message_.messageFile = _cloud_file_dto_.fileid;
                            $scope._message_update_(_message_);
                        }
                    }
                }
            }, function (_resp_) {
            }, function (_evt_) {
            });
        };
        $scope._message_delete_file_ = function(_message_){
            attach_delete(_message_.messageFile);
            _message_.messageFile = null;
            $scope._message_update_(_message_);
            $scope.initMessage($scope.message_id_decode);
            $location.path("/message/share/" + $routeParams.id);
        };
        $scope._message_update_ = function(_message_){
            $http({
                method: 'post',
                url: srvUrl + 'message/update.do',
                data: $.param(_message_),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data) {
                $scope.initMessage($scope.message_id_decode);
                $location.path("/message/share/" + $routeParams.id);
            });
        };
        $scope._message_preview_file_ = function(_url_, fullpath){
            /*if(!isEmpty(_url_)){
                $window.open(_url_);
            }*/
            $.ajax({
                url: srvUrl + 'cloud/getUrl.do',
                type: "POST",
                dataType: "json",
                data: {"type": 'preview', 'path': fullpath},
                async: true,
                success: function (data) {
                    if(!isEmpty(data)){
                        $window.open(data);
                    }
                }
            });
        };
        $scope._private_setTips = function(subjectId){
            window.setTimeout(function(){
                $('#_message_panel_body_' + subjectId).addClass('in');
            }, 2000);
        };
    }]);