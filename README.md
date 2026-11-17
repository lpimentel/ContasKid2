# ContasKid2

Aplicação Android de matemática para crianças. Inclui seis níveis de adição, subtração e multiplicação, três vidas por partida, pontuação e um recorde local em SQLite. Suporta português, inglês e espanhol.

## Histórico

A versão original de fevereiro de 2019 está preservada na branch `master`. A branch `fix/android-modernization-2026` moderniza o projeto e refatora a lógica partilhada.

## Requisitos de desenvolvimento

- Android Studio compatível com Android Gradle Plugin 8.11
- JDK 17
- Android SDK 36
- Gradle 8.13 (wrapper incluído)
- AndroidX AppCompat

Abrir este diretório no Android Studio, sincronizar o Gradle e executar o módulo `app`. Em ambiente com SDK e JDK configurados, executar:

```bash
./gradlew testDebugUnitTest assembleDebug assembleDebugAndroidTest
```

O APK de desenvolvimento, após compilação, ficará em `app/build/outputs/apk/debug/`.

## Funcionamento

O jogador começa com três vidas, perde uma por resposta errada e passa de nível depois de dez respostas certas. Conclui o jogo com 60 pontos. O recorde fica guardado numa base SQLite local.

## Testes e estado de validação

Os testes unitários verificam regras aritméticas e limites dos níveis. O projeto possui um workflow de GitHub Actions que executa testes unitários e compila o APK. A execução em dispositivo ou emulador Android continua necessária para verificar interfaces, áudio, compatibilidade e usabilidade.

## Privacidade

A aplicação guarda apenas o nome introduzido e a pontuação localmente. Não declara permissões de rede. As cópias de segurança automáticas estão desativadas.
