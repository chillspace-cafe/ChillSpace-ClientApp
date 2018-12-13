package chillspace.chillspace.models

class User {

    lateinit var username: String
    lateinit var email: String

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    constructor() {}

    constructor(name: String, email: String) {
        this.username = name
        this.email = email
    }
}