import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import FormField from "@/features/auth/components/FormField";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { roleHome, ROLES } from "@/lib/auth/roles";
import { devLoginAs } from "@/lib/auth/devLogin";
import { extractApiError } from "@/lib/helpers/helpers";
import { validateEmail, validatePassword, collectErrors } from "@/lib/helpers/validators";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [form, setForm] = useState({ email: "", password: "" });
  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const update = (field) => (e) => {
    setForm((f) => ({ ...f, [field]: e.target.value }));
    setErrors((prev) => ({ ...prev, [field]: "" }));
    setFormError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const fieldErrors = collectErrors({
      email: validateEmail(form.email),
      password: validatePassword(form.password),
    });
    if (Object.keys(fieldErrors).length) {
      setErrors(fieldErrors);
      return;
    }

    setSubmitting(true);
    setFormError("");
    try {
      const user = await login(form);
      // Redirect to the originally requested page, or the role's home.
      const from = location.state?.from?.pathname;
      navigate(from ?? roleHome(user?.role), { replace: true });
    } catch (err) {
      setFormError(extractApiError(err, "Login failed. Check your email and password."));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-muted/30 p-4">
      <Card className="w-full max-w-sm">
        <CardHeader>
          <CardTitle className="text-lg">Welcome back</CardTitle>
          <CardDescription>Sign in to your Medicine Locator account</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} noValidate className="flex flex-col gap-4">
            <FormField
              id="email"
              label="Email"
              type="email"
              autoComplete="email"
              placeholder="you@example.com"
              value={form.email}
              onChange={update("email")}
              error={errors.email}
            />
            <FormField
              id="password"
              label="Password"
              type="password"
              autoComplete="current-password"
              placeholder="••••••••"
              value={form.password}
              onChange={update("password")}
              error={errors.password}
            />

            {formError && (
              <p className="rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">
                {formError}
              </p>
            )}

            <Button type="submit" size="lg" disabled={submitting} className="w-full">
              {submitting ? "Signing in…" : "Sign in"}
            </Button>
          </form>

          <p className="mt-4 text-center text-sm text-muted-foreground">
            Don&apos;t have an account?{" "}
            <Link to="/register" className="font-medium text-primary hover:underline">
              Create one
            </Link>
          </p>

          {import.meta.env.DEV && (
            <div className="mt-6 border-t pt-4">
              <p className="mb-2 text-center text-xs font-medium text-muted-foreground">
                Dev preview (no backend) — enter as:
              </p>
              <div className="grid grid-cols-3 gap-2">
                <Button variant="outline" size="sm" onClick={() => devLoginAs(ROLES.CUSTOMER)}>
                  Customer
                </Button>
                <Button variant="outline" size="sm" onClick={() => devLoginAs(ROLES.PHARMACY)}>
                  Pharmacy
                </Button>
                <Button variant="outline" size="sm" onClick={() => devLoginAs(ROLES.ADMIN)}>
                  Admin
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
