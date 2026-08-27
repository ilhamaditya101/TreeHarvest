# TreeHarvest

Paper 1.20.1 / Java 17.

## Features
- Confirm GUI, then 9-slot chopping minigame.
- 8 red panes + 1 green pane.
- Successful chop removes all connected logs, then restores the exact original log materials.
- Configurable +300 AuraSkills Foraging XP by default.
- Leaves never permanently disappear.
- Leaf cooldown is **per-player + per-tree**, default 60 seconds.
- A player can immediately harvest another tree while the first tree is on cooldown.
- Vanilla rewards and an MMOItems reward configuration hook.
- GitHub Actions workflow builds the JAR.

## Important
The MMOItems section is a hook because MMOItems API signatures vary between releases. Replace the small MMOItems branch in `TreeListener.giveRewards()` with the API call matching the MMOItems version on your server.

## Build
GitHub Actions runs `gradle build` and uploads `build/libs/TreeHarvest-1.0.0.jar` as an artifact.
