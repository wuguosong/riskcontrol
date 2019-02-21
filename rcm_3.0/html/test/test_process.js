function test_process_commonAjax(type, url, data, success, error) {
    $.ajax({
        type: type,
        url: url,
        data: data,
        success: function (data) {
            success(data);
        },
        error: function (data) {
            error(data);
        },
        async: false
    });
}

function test_process_detail() {
    var result = null;
    test_process_commonAjax('post', 'http://localhost:8080/rcm-rest/process/detail.do', {
        'processKey': 'preAssessment',
        'businessKey': '58c7909122ddf2207a7cd6c9'
    }, function (data) {
        result = data;
    }, function (data) {
        console.log(data);
    });
    return result;
}

function test_process_config() {
    var result = null;
    test_process_commonAjax('post', 'http://localhost:8080/rcm-rest/process/config.do', {
        'processKey': 'preAssessment',
        'businessKey': '58c7909122ddf2207a7cd6c9'
    }, function (data) {
        result = data;
    }, function (data) {
        console.log(data);
    });
    return result;
}

function test_process_allConfig(){
    var result = null;
    test_process_commonAjax('post', 'http://localhost:8080/rcm-rest/process/allConfig.do', {
        'processKey': 'bulletin',
        'businessKey': '35b6f36b226644149b78704555d39a8e'
    }, function (data) {
        result = data;
    }, function (data) {
        console.log(data);
    });
    return result;
}