const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// Thresholds (must match BudgetAlertHelper.kt)
const WARN_1 = 80;
const WARN_2 = 90;
const WARN_3 = 100;

exports.budgetAlert = functions.firestore
    .document("users/{userId}/trips/{tripId}")
    .onUpdate(async (change, context) => {
        const before = change.before.data();
        const after = change.after.data();

        if (!after.totalBudget || after.totalBudget <= 0) return null;

        const spentBefore = before.totalBudget - before.remainingBudget;
        const spentAfter = after.totalBudget - after.remainingBudget;

        const pctBefore = (spentBefore / after.totalBudget) * 100;
        const pctAfter = (spentAfter / after.totalBudget) * 100;

        let crossed = null;
        if (pctBefore < WARN_1 && pctAfter >= WARN_1) crossed = WARN_1;
        else if (pctBefore < WARN_2 && pctAfter >= WARN_2) crossed = WARN_2;
        else if (pctBefore < WARN_3 && pctAfter >= WARN_3) crossed = WARN_3;

        if (!crossed) return null;

        const userDoc = await admin.firestore()
            .collection("users").doc(context.params.userId).get();
        const token = userDoc.get("fcmToken");
        if (!token) return null;

        const message = {
            notification: {
                title: `FarePlan SA - ${after.destination}`,
                body: crossed === WARN_3
                    ? `You've spent 100% of your budget!`
                    : `You've spent ${crossed}% of your budget.`,
            },
            token: token,
        };

        return admin.messaging().send(message);
    });