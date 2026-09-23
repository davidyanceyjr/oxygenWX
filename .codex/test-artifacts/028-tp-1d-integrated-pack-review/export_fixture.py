from pathlib import Path
import subprocess
p=Path(__file__).resolve().parent
c=Path.home()/'.gradle/caches/modules-2/files-2.1'
cp=':'.join(['app/build/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes',str(next(c.glob('org.jetbrains.kotlin/kotlin-stdlib/2.4.20/**/*.jar'))),str(next(c.glob('com.google.code.gson/gson/2.11.0/**/*.jar')))])
r=subprocess.run(['/usr/lib/jvm/java-27-openjdk/bin/java','-cp',cp,str(p/'ExportFixture.java')],capture_output=True,text=True,check=True)
(p/'fixture.json').write_text(r.stdout)
