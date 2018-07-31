const functions = require('firebase-functions')
const admin = require('firebase-admin')
admin.initializeApp(functions.config().firebase)

const users = 'users'
const admins = 'admins'
const notifications = 'notifications'
const contents = 'contents'
const tokens = 'tokens'

const refWithParams = `/${users}/${notifications}/${contents}/{user_id}/{notification_id}`
const refFree = `/${users}/${notifications}`

exports.sendNotification = functions.database.ref(refWithParams)
.onWrite((change, context) => {

    const userAdminId = context.params.user_id
    const notificationId = context.params.notification_id

    console.log("User ID: " + userAdminId + " Noti ID: " + notificationId)

    const afterVal = change.after.val()
    if (!afterVal) {
        return console.log('A notification has been deleted from database: ', notification_id);
    }

    const fromUser = admin.database().ref(`/${users}/${notifications}/${contents}/${userAdminId}/${notificationId}`).once('value')
    return fromUser.then(fuResult => {

        const from = fuResult.val().from
        const to = fuResult.val().to
        const msg = fuResult.val().message

        console.log("F: " + from + " T: " + to + " M: " + msg)

        const userIds = admin.database().ref(`/${users}/${notifications}/${tokens}/${to}`).once('value')
        return userIds.then(uiResult => {
            const adminUserId = uiResult.key

            uiResult.forEach(result => {
                const tokenId = result.val().token_id
                const deviceId = result.key

                console.log("CHECK ------- DI: " + deviceId + " TI: " + tokenId)

                Promise.all([msg, tokenId]).then(groups => {
                    const msg = groups[0]
                    const token = groups[1]

                    console.log("MSG: " + msg + " TOK: " + token)

                    const payload = {
                        notification: {
                            title: "New Work",
                            body:`${msg}`,
                            icon: "default"
                        },
                        data: {
                            device_id: deviceId
                        }
                    }

                    admin.messaging().sendToDevice(tokenId, payload).then(res => {
                        console.log("Notification sent succssfully " + res)
                    }).catch((err) => {
                        console.log("Notification not sent " + err)
                    })
                })
            })
        })
    })
})