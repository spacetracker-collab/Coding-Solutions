import torch

def get_hamiltonian_flow(model, x, p):
    """
    Implements Hamilton's Equations (Eq 17):
    dx/dt = dH/dp
    dp/dt = -dH/dx
    """
    # Ensure variables track gradients for the Hamiltonian calculation
    if not x.requires_grad: x.requires_grad_(True)
    if not p.requires_grad: p.requires_grad_(True)
    
    # Potential Energy V(x) = -P(x) 
    # (The AI wants to minimize 'Potential', which means maximizing 'Preference')
    pref_intensity = model(x)
    
    # Hamiltonian H(x, p) = Kinetic + Potential
    # H = 0.5 * p^2 - P(x)
    H = 0.5 * torch.sum(p**2) - pref_intensity.sum()
    
    # Hamilton's Equations using autograd
    # create_graph=True is required for higher-order gradient tracking
    dx_dt = torch.autograd.grad(H, p, create_graph=True)[0]
    dp_dt = -torch.autograd.grad(H, x, create_graph=True)[0]
    
    return dx_dt, dp_dt

def simulate_phase_space(model, steps=50, dt=0.1):
    # Starting position (x) and initial momentum (p)
    x = torch.tensor([[1.0, 1.0]], requires_grad=True) 
    p = torch.tensor([[0.1, -0.1]], requires_grad=True) 
    
    trajectory = []
    for _ in range(steps):
        dx, dp = get_hamiltonian_flow(model, x, p)
        with torch.no_grad():
            x = x + dx * dt
            p = p + dp * dt
        trajectory.append(x.clone().detach().numpy())
    return trajectory
