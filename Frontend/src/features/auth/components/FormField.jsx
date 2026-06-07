import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";

/** Labeled input with inline validation error, used across the auth forms. */
export default function FormField({
  id,
  label,
  error,
  className,
  ...inputProps
}) {
  return (
    <div className={cn("flex flex-col gap-1.5", className)}>
      <label htmlFor={id} className="text-sm font-medium text-foreground">
        {label}
      </label>
      <Input id={id} aria-invalid={!!error} {...inputProps} />
      {error && <p className="text-xs text-destructive">{error}</p>}
    </div>
  );
}
