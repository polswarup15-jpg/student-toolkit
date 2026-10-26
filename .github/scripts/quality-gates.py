#!/usr/bin/env python3
"""Static V1 quality gates. Runtime/device gates remain separate."""
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[2]
errors = []
def check(condition, message):
    print(("PASS" if condition else "FAIL") + ": " + message)
    if not condition:
        errors.append(message)

manifest = ET.parse(root / "app/src/main/AndroidManifest.xml").getroot()
android = "{http://schemas.android.com/apk/res/android}"
permissions = {node.get(android + "name") for node in manifest.findall("uses-permission")}
approved = {
    "android.permission.SYSTEM_ALERT_WINDOW",
    "android.permission.FOREGROUND_SERVICE",
    "android.permission.FOREGROUND_SERVICE_SPECIAL_USE",
}
check(not (permissions - approved), "No unreviewed manifest permissions: " + str(sorted(permissions - approved)))
check("android.permission.INTERNET" not in permissions, "Offline-first V1 has no INTERNET permission")
app = manifest.find("application")
check(app is not None and app.get(android + "allowBackup") == "false", "Local data is not implicitly backed up")
check(app is not None and app.get(android + "usesCleartextTraffic") == "false", "Cleartext traffic disabled")
check((root / "docs/v1-quality-benchmarks.md").is_file(), "Quality criteria documented")
tests = list((root / "app/src/androidTest").rglob("*Test.java"))
check(len(tests) >= 8, "At least eight instrumentation test classes present (presence only, not passing)")
for name in ("Subjects", "Homework", "Notes", "Timetable", "Exams", "Focus", "Progress", "SettingsPrivacy"):
    check(any(name in path.name for path in tests), name + " instrumentation test present")
print(f"Static quality gate: {len(errors)} failure(s). Runtime and physical-device verification still required.")
sys.exit(bool(errors))
