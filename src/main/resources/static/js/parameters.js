var showGroups = function() {
    $.ajax({
        type: "GET",
        cache: false,
        url: '/group/fetch',
        data: "",
        success: function (response) {
            var html = "<span class='pure-menu-heading'>Parameters</span><ul class='pure-menu-list'>";
            $.each(response.data, function (i) {
                var name = response.data[i].name;
                var id = response.data[i].id;
                html = html + "<li class='pure-menu-item'><a class='pure-menu-link' onclick=chooseGroup(" + id + ",'" + name + "')>" + name + "</a></li>";
            });
            html = html + "<li class='pure-menu-item'><a class='pure-menu-link' onclick=showAddGroup()>+</a></li></ul>";
            $('#groups').html(html);
        }
    });
}

var chooseGroup = function(groupId, gName) {
    $('#parameters').html("");
    $('#add_parameter').html("<a onclick=showAddParameter(" + groupId + ",'" + gName + "')>+ parameter of " + gName + "</a>");
    $.ajax({
        type: "GET",
        cache: false,
        url: '/instance/' + groupId,
        data: "",
        success: function (response) {
        var html = "<span class='pure-menu-heading'>" + gName + "</span><ul class='pure-menu-list'>";
        $.each(response.data, function (i) {
            var name = response.data[i].name;
            var id = response.data[i].id;
            html = html + "<li class='pure-menu-item'><a class='pure-menu-link' onclick=chooseParams(" + id + ",'" + name + "')>" + name + "</a></li>";
        });
        html = html + "<li class='pure-menu-item'><a class='pure-menu-link' onclick=showAddGroupInstance(" + groupId + ",'" + gName + "')>+</a></li></ul>";
        $('#instances').html(html);
        }
    });
}

var chooseParams = function(instanceId, iName) {
    var filter = "";
    if (document.getElementById('parameter_filter') != null) {
        filter = document.getElementById('parameter_filter').value;
    }
    $.ajax({
        type: "GET",
        cache: false,
        url: '/parameters/' + instanceId,
        data: {'filter':filter},
        success: function (response) {
            var html = "<table id='parameters_table' instanceId=" + instanceId + " iName='" + iName + "' class='pure-table'>" +
            "<thead><tr><th><input onchange=chooseParams(" + instanceId + ",'" + iName + "') size=30 id='parameter_filter' type='text' value='" + filter + "'></th><th></th><th>" + iName + "</th></tr></thead>";
            $.each(response.data, function (i) {
                var value = "";
                var parameterId = response.data[i].parameter.id;
                if (response.data[i].value != null) {
                    value = response.data[i].value.value;
                    id = response.data[i].value.id;
                }
                var description = "";
                if (response.data[i].parameter.description != null) {
                    description =  response.data[i].parameter.description;
                }
                var name = response.data[i].parameter.name;
                html = html + "<tr><td>" + name + "</td><td><div id='i_" +
                instanceId + "_p_" + parameterId +
                "'><a onclick=editParam(" + instanceId + "," + parameterId + ",'" + value + "')>#</a> " + value + "</td>" +
                "<td>" + description + "</td></tr>";
            });
            html = html + "</table>";
            $('#parameters').html(html);
        }
    });
    setLink(instanceId, iName);
}

var showAddGroup = function(){
    var html = "<form class='pure-form pure-form-stacked'>" +
    "<fieldset><legend>Add new group</legend>" +
    "<input id='addGroupName' type='text' placeholder='Group Name'>" +
    "<input id='description' type='text' placeholder='Description'>" +
    "<button type='button' onclick=saveGroup()  " +
    "class='pure-button pure-button-primary'>Save group</button>" +
    " <button type='button' onclick=clearCenterContent()  " +
        "class='pure-button pure-button-primary'>Close</button></fieldset>";
    $('#center-content').html(html);
}

var showAddGroupInstance = function(id,name){
    var html = "<form class='pure-form pure-form-stacked'>" +
    "<fieldset><legend>Add new instance for " + name + "</legend>" +
    "<input id='addInstanceName' type='text' placeholder='Instance Name'>" +
    "<input id='description' type='text' placeholder='Description'>" +
    "<button type='button' onclick=saveGroupInstance(" + id + ",'" + name + "')  " +
    "class='pure-button pure-button-primary'>Save instance</button>" +
    " <button type='button' onclick=clearCenterContent()  " +
        "class='pure-button pure-button-primary'>Close</button></fieldset>";
    $('#center-content').html(html);
}

var showAddParameter = function(id, name){
    var html = "<form class='pure-form pure-form-stacked'>" +
    "<fieldset><legend>Add new parameter for " + name + "</legend>" +
    "<input id='parameterName' type='text' placeholder='Parameter'>" +
    "<input id='description' type='text' placeholder='Description'>" +
    "<button type='button' onclick=saveParameter(" + id + ",'" + name + "')  " +
    "class='pure-button pure-button-primary'>Save parameter</button>" +
    " <button type='button' onclick=clearCenterContent()  " +
        "class='pure-button pure-button-primary'>Close</button></fieldset>";
    $('#center-content').html(html);
}

var clearCenterContent = function() {
    $('#center-content').html("");
}

var saveGroup = function() {
    var name = document.getElementById('addGroupName').value;
    var description = document.getElementById('description').value;
    $.ajax({
        type: "POST",
        cache: false,
        url: '/group',
        data: {'groupName':name, 'description':description},
        success: function (response) {
            if (response.result == "success") {
                showGroups();
                clearCenterContent()
            }
            else {
                alert(response);
            }
        }
    });
}

var saveGroupInstance = function(id,gName) {
    var name = document.getElementById('addInstanceName').value;
    var description = document.getElementById('description').value;
    $.ajax({
        type: "POST",
        cache: false,
        url: '/groupInstance',
        data: {'groupInstanceName':name, 'description':description, 'groupId': id},
        success: function (response) {
            if (response.result == "success") {
                chooseGroup(id,gName);
                clearCenterContent()
            }
            else {
                alert(response);
            }
        }
    });
}

var showParam = function(instanceId, parameterId, value) {
    $('#i_' + instanceId + '_p_' + parameterId).html("<a onclick=editParam(" + instanceId + "," + parameterId + ",'" + value + "')>#</a> " + value);
}

var editParam = function(instanceId, parameterId, value) {
    var html = "<a onclick=showParam(" + instanceId + "," + parameterId + ",'" + value + "')># </a><input size=40 id='new_value_of_i_" + instanceId + "_p_" + parameterId +"' onchange=changeParameter(" + instanceId + "," + parameterId + ") value='" + value + "'>";
    $('#i_' + instanceId + '_p_' + parameterId).html(html);
}

var changeParameter = function(instanceId, parameterId) {
    var newValue = document.getElementById("new_value_of_i_" + instanceId + "_p_" + parameterId).value;
    $.ajax({
            type: "POST",
            cache: false,
            url: '/setParameter',
            data: {'instanceId':instanceId, 'parameterId':parameterId, 'value': newValue},
            success: function (response) {
                if (response.result == "success") {
                    showParam(instanceId, parameterId, newValue);
                }
                else {
                    alert(response);
                }
            }
        });
}

var saveParameter = function(groupId, name) {
    var name = document.getElementById('parameterName').value;
    var description = document.getElementById('description').value;
    $.ajax({
            type: "POST",
            cache: false,
            url: '/addParameter',
            data: {'groupId':groupId, 'name':name, 'description': description},
            success: function (response) {
                if (response.result == "success") {
                    if (document.getElementById('parameters_table') != null) {
                        var instanceId =document.getElementById('parameters_table').getAttribute('instanceid');;
                        var iName = document.getElementById('parameters_table').getAttribute('iname');;
                        chooseParams(instanceId, iName);
                    }
                    clearCenterContent()
                }
                else {
                    alert(response);
                }
            }
        });
}

var setLink = function(instanceId, name) {
    var url = name + ": <i>" + document.URL + "parameters/" + instanceId + "</i>";
    $('#link_for_tests').html(url);
}
