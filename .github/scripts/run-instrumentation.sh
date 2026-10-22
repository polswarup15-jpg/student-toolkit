#!/usr/bin/env bash
set -o pipefail
adb wait-for-device
adb shell getprop sys.boot_completed
adb shell input keyevent 82
set +e
echo "=== Run Settings + Privacy in isolation ==="
gradle :app:connectedDebugAndroidTest --no-daemon --stacktrace --info -Pandroid.testInstrumentationRunnerArguments.class=com.studenttoolkit.app.SettingsPrivacyTest 2>&1 | tee instrumentation.log
SETTINGS_RESULT=${PIPESTATUS[0]}
if [ "$SETTINGS_RESULT" -eq 0 ]; then
  echo "=== Run complete instrumentation suite ===" | tee -a instrumentation.log
  gradle :app:connectedDebugAndroidTest --no-daemon --stacktrace --info 2>&1 | tee -a instrumentation.log
  RESULT=${PIPESTATUS[0]}
else
  echo "Settings test failed; preserving isolated failure diagnostics" | tee -a instrumentation.log
  RESULT="$SETTINGS_RESULT"
fi
if [ "$RESULT" -ne 0 ]; then
  echo "=== Instrumentation crash diagnostics ==="
  adb logcat -d -t 1500 -v time | grep -E -A 12 -B 8 'FATAL EXCEPTION|AndroidRuntime|INSTRUMENTATION_FAILED|Process crashed|com.studenttoolkit.app|TestRunner' | tail -350 || true
  echo "=== Test result files ==="
  find app/build/outputs/androidTest-results app/build/reports/androidTests -type f 2>/dev/null | sort || true
fi
exit "$RESULT"
