#!/bin/bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="$PROJECT_DIR/build"
RAYLIB_DIR="/tmp/raylib-src"

echo "=== SpaCE Invaders - Instalando dependencias ==="

# Dependencias del sistema
if [ -f /etc/debian_version ]; then
    echo "[1/4] Instalando paquetes del sistema..."
    sudo apt update
    sudo apt install -y \
        build-essential \
        cmake \
        libgl1-mesa-dev \
        libxi-dev \
        libxcursor-dev \
        libxinerama-dev \
        libxrandr-dev \
        libx11-dev \
        libasound2-dev \
        mesa-common-dev \
        uuid-dev \
        libglfw3-dev \
        pkg-config \
        git
elif [ -f /etc/arch-release ]; then
    sudo pacman -S --needed \
        base-devel cmake mesa libxi libxcursor libxinerama \
        libxrandr libx11 libgl uuid glfw
elif [ -f /etc/redhat-release ]; then
    sudo dnf install -y \
        cmake gcc mesa-libGL-devel libXi-devel libXcursor-devel \
        libXinerama-devel libXrandr-devel libX11-devel \
        alsa-lib-devel uuid-devel glfw-devel
fi

# Raylib (compilar desde fuente porque no está en los repos oficiales)
if ! pkg-config --exists raylib 2>/dev/null && [ ! -f /usr/lib/x86_64-linux-gnu/libraylib.so ]; then
    echo "[2/4] Descargando raylib..."
    git clone --depth 1 --branch 6.0.0 https://github.com/raysan5/raylib.git "$RAYLIB_DIR"

    echo "[3/4] Compilando raylib..."
    mkdir -p "$RAYLIB_DIR/build"
    cmake -S "$RAYLIB_DIR" -B "$RAYLIB_DIR/build" \
        -DBUILD_SHARED_LIBS=ON \
        -DCMAKE_BUILD_TYPE=Release
    cmake --build "$RAYLIB_DIR/build" --parallel "$(nproc)"

    echo "[4/4] Instalando raylib..."
    sudo cmake --install "$RAYLIB_DIR/build"

    rm -rf "$RAYLIB_DIR"
else
    echo "[2/4] raylib ya está instalado, saltando..."
fi

# Compilar el juego
echo "[+] Compilando SpaCE Invaders..."
mkdir -p "$BUILD_DIR"
cmake -S "$PROJECT_DIR" -B "$BUILD_DIR" -DCMAKE_BUILD_TYPE=Release
cmake --build "$BUILD_DIR" --parallel "$(nproc)"

echo ""
echo "=== Listo! Ejecuta: ./build/game ==="
