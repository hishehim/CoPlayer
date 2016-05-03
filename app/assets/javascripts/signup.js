$.validator.setDefaults({
});

//This method is to check input against regular expressions
//to prevent input of invalid characters
$.validator.addMethod(
    "regex",
        function(value, element, regexp) {
            var re = new RegExp(regexp);
            return this.optional(element) || re.test(value);
        },
    "Please make sure you use valid characters [a-z A-Z 0-9 _ - ]."
);

//This method compares two value to make sure they are not the same
$.validator.addMethod(
    "notEqualTo",
        function(value, element,param){
        return this.optional(element) || value != $(param).val();
        },
    "Please enter different values for password."
);

$().ready(function() {
    // validate signup form on keyup and submit
    $("#signupForm").validate({
        rules: {
            username: {
                required: true,
                minlength: 4,
                maxlength: 40,
                regex: "^[a-zA-Z0-9_-]{4,40}$"
            },
            password: {
                required: true,
                minlength: 8,
                maxlength: 256,
                regex: "^[a-zA-Z0-9_-]{8,256}$",
                notEqualTo: "#username"
            },
            confirm_password: {
                required: true,
                minlength: 8,
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
                minlength: "Your username must consist of at least 8 characters",
                maxlength: "The username should consist of less than 15 characters"
            },
            password: {
                required: "Please provide a password",
                minlength: "Your password must be at least 8 characters long",
            },
            confirm_password: {
                required: "Please provide a password",
                minlength: "Your password must be at least 8 characters long",
                equalTo: "Please enter the same password as above"
            },
            email: "Please enter a valid email address",
        }
    });
});


