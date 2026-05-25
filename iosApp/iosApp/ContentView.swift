import SwiftUI
import UIKit
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @State private var showSplash = true
    @State private var logoScale: CGFloat = 0.85
    @State private var logoOpacity: Double = 0.0
    @State private var glowScale: CGFloat = 0.8
    @State private var textOpacity: Double = 0.0

    var body: some View {
        ZStack {
            // Fondo oscuro común para que no haya parpadeos al cambiar de vista
            Color(red: 0.05, green: 0.05, blue: 0.12)
                .ignoresSafeArea()

            if !showSplash {
                ComposeView()
                    .ignoresSafeArea()
                    .transition(.opacity.combined(with: .scale(scale: 0.97)))
            } else {
                // Pantalla de carga / Splash Screen Premium
                ZStack {
                    // Gradiente de noche profunda con violetas
                    LinearGradient(
                        colors: [
                            Color(red: 0.05, green: 0.05, blue: 0.12),
                            Color(red: 0.08, green: 0.06, blue: 0.18),
                            Color(red: 0.04, green: 0.04, blue: 0.10)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                    .ignoresSafeArea()

                    // Estrellas de fondo sutiles
                    GeometryReader { geo in
                        ForEach(0..<20) { i in
                            Circle()
                                .fill(Color.white.opacity(Double.random(in: 0.2...0.6)))
                                .frame(width: CGFloat.random(in: 1.5...3), height: CGFloat.random(in: 1.5...3))
                                .position(
                                    x: CGFloat.random(in: 0...geo.size.width),
                                    y: CGFloat.random(in: 0...geo.size.height)
                                )
                        }
                    }
                    .ignoresSafeArea()

                    VStack(spacing: 24) {
                        Spacer()

                        // Logotipo animado con brillos (Círculo de pulso + Luna y ondas de sueño)
                        ZStack {
                            // Círculo de brillo pulsante en el fondo
                            Circle()
                                .fill(
                                    RadialGradient(
                                        colors: [Color.purple.opacity(0.25), Color.clear],
                                        center: .center,
                                        startRadius: 5,
                                        endRadius: 90
                                    )
                                )
                                .frame(width: 220, height: 220)
                                .scaleEffect(glowScale)

                            // Luna de SwiftUI y ondas
                            VStack(spacing: 8) {
                                Image(systemName: "moon.stars.fill")
                                    .font(.system(size: 80))
                                    .foregroundStyle(
                                        LinearGradient(
                                            colors: [.white, Color(red: 0.7, green: 0.6, blue: 1.0)],
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        )
                                    )
                                    .shadow(color: .purple.opacity(0.6), radius: 15, x: 0, y: 0)
                            }
                            
                            // Ondas de sueño decorativas
                            ForEach(0..<3) { i in
                                Circle()
                                    .stroke(Color.purple.opacity(0.15 - Double(i) * 0.04), lineWidth: 1.5)
                                    .frame(width: CGFloat(130 + i * 30), height: CGFloat(130 + i * 30))
                                    .scaleEffect(glowScale)
                            }
                        }
                        .scaleEffect(logoScale)
                        .opacity(logoOpacity)

                        VStack(spacing: 12) {
                            Text("SOMNOS AI")
                                .font(.system(size: 34, weight: .bold, design: .rounded))
                                .foregroundColor(.white)
                                .tracking(6)
                                .shadow(color: .black.opacity(0.3), radius: 5, x: 0, y: 3)

                            Text("Inteligencia Artificial para tu Descanso")
                                .font(.system(size: 14, weight: .medium, design: .rounded))
                                .foregroundColor(Color(red: 0.65, green: 0.62, blue: 0.78))
                                .tracking(1)
                        }
                        .opacity(textOpacity)

                        Spacer()
                        
                        // Indicador de carga sutil
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: Color.purple))
                            .scaleEffect(1.2)
                            .opacity(textOpacity)
                            .padding(.bottom, 50)
                    }
                }
                .onAppear {
                    // Animación del logo (Fade in y rebote sutil)
                    withAnimation(.easeOut(duration: 1.0)) {
                        logoOpacity = 1.0
                        logoScale = 1.0
                    }
                    
                    // Animación de brillo infinito
                    withAnimation(.easeInOut(duration: 2.0).repeatForever(autoreverses: true)) {
                        glowScale = 1.15
                    }

                    // Animación de textos retrasada
                    withAnimation(.easeOut(duration: 0.8).delay(0.4)) {
                        textOpacity = 1.0
                    }

                    // Transición a la app principal
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2.8) {
                        withAnimation(.easeInOut(duration: 0.6)) {
                            showSplash = false
                        }
                    }
                }
            }
        }
    }
}

