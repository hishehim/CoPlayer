$.validator.setDefaults({
});

//This method is to check input against regular expressions
//to prevent input of invalid characters
$.validator.addMethod(
    "regex",
        function(value, element, regexp) {
            var re = new RegExp(regexp);
            return this.optional(element) || re.test(value);
        }, null
);

//This method compares two value to make sure they are not the same
$.validator.addMethod(
    "notEqualTo",
        function(value, element,param){
        return this.optional(element) || value != $(param).val();
        }, null
);

$().ready(function() {
    // validate signup form on keyup and submit
    $("#signupForm").validate({
        rules: {
            username: {
                required: true,
                minlength: 4,
                maxlength: 40,
                regex: "^[a-zA-Z0-9]{4,40}$"
            },
            password: {
                required: true,
                minlength: 8,
                maxlength: 256,
                regex: "^[a-zA-Z0-9!@#$%^&*)(+=._-]{8,256}$",
                notEqualTo: "#username"
            },
            confirm_password: {
                required: true,
                minlength: 8,
                maxlength: 256,
                equalTo: "#password"
            },
            email: {
                required: true,
                email: true
            },
        },
        messages: {
            username: {
                required: "Please enter a username",
                minlength: "Your username must consist of at least 4 characters",
                maxlength: "The username should consist of less than 40 characters",
                regex: "Letters and numbers only!"
            },
            password: {
                required: "Please provide a password",
                minlength: "Your password must be at least 8 characters long",
                maxlength: "The password should consist of less than 256 characters",
                regex: "Only alphanumeric and !@#$%^&*)(+=._- are allowed",
            },
            confirm_password: {
                required: "Please comfirm password",
                minlength: "Your password must be at least 8 characters long",
                maxlength: "The password should consist of less than 256 characters",
                equalTo: "Please enter the same password as above",
                regex: "Only alphanumeric and !@#$%^&*)(+=._- are allowed",
            },
            email: "Please enter a valid email address",
        }
    });
});


