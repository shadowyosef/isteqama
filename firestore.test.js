const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE = "alice_123";
const BOB = "bob_456";
const [host, portStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const port = parseInt(portStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: { rules, host, port },
  });
});

after(async () => { if (testEnv) await testEnv.cleanup(); });
beforeEach(async () => { if (testEnv) await testEnv.clearFirestore(); });

test("Unauthenticated user: cannot read backups or circles", async () => {
  const unauth = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauth.collection("users").doc(ALICE).collection("backups").get());
  await assertFails(unauth.collection("circles").get());
});

test("User backups: owner can write & read, cross-user denied", async () => {
  const alice = testEnv.authenticatedContext(ALICE).firestore();
  const bob = testEnv.authenticatedContext(BOB).firestore();
  const ref = alice.collection("users").doc(ALICE).collection("backups").doc("latest_device_sync");
  await assertSucceeds(ref.set({
    ownerId: ALICE, label: "Pixel", totalTasbeeh: 120, totalPagesRead: 4,
    currentStreak: 3, locksCount: 2, updatedAt: new Date()
  }));
  await assertSucceeds(ref.get());
  await assertFails(bob.collection("users").doc(ALICE).collection("backups").doc("latest_device_sync").get());
  await assertFails(bob.collection("users").doc(ALICE).collection("backups").doc("latest_device_sync").delete());
});

test("Shared circles: creator & joiner allowed, blanket list denied", async () => {
  const alice = testEnv.authenticatedContext(ALICE).firestore();
  const bob = testEnv.authenticatedContext(BOB).firestore();
  const circleRef = alice.collection("circles").doc("HDA74K");
  await assertSucceeds(circleRef.set({
    ownerId: ALICE, title: "Ramadan Circle", inviteCode: "HDA74K",
    targetDays: 30, totalPagesRead: 0, memberUids: [ALICE],
    createdAt: new Date(), updatedAt: new Date()
  }));
  await assertFails(bob.collection("circles").get());
  await assertSucceeds(bob.collection("circles").doc("HDA74K").update({
    memberUids: [ALICE, BOB], updatedAt: new Date()
  }));
  await assertSucceeds(bob.collection("circles").where("memberUids", "array-contains", BOB).get());
  await assertFails(bob.collection("circles").doc("HDA74K").delete());
});
