# Jadx Ollama Assist Plugin

A Jadx GUI plugin that connects decompiled code with a local Ollama model for assisted reverse engineering, code analysis, and Kotlin integration guidance.

## What this repository contains

- A Jadx plugin implementation in Java
- A Swing-based UI for asking the Ollama model questions about decompiled code
- A settings dialog for endpoint, model, and temperature
- Support for contextual code analysis and custom prompts

## Build

Build the plugin JAR from the repository root:

```powershell
./gradlew jar
```

Then copy the generated artifact from `build/libs/` into your Jadx `plugins` folder.

## Quick start

1. Start Jadx GUI with Java 11 or newer.
2. Make sure your local Ollama service is running.
3. Right-click a decompiled node in Jadx and choose `Analyze with Ollama`.
4. Use the analysis panel to provide custom questions or select a built-in mode.

## Plugin behavior

- `Analyze with Ollama` opens a panel containing the selected decompiled code context.
- The dialog sends a prompt to the local Ollama API.
- You can customize the prompt with direct instructions like:
  - `How can I use this in a Kotlin Android app?`
  - `Generate an example Kotlin integration.`

## Settings

The plugin adds an `Ollama Settings` menu item in Jadx.

Settings stored in `~/.ollama/ollama.toml` include:

- `endpoint` — the Ollama service URL
- `model` — the model name to use
- `temperature` — the model sampling temperature

## Notes

- This plugin requires Java 11 at runtime due to the Jadx core dependency.
- Use clear, task-oriented prompts for best results.
- Documentation and testing are separated into dedicated branches to keep the repository organized.
