export const getBreadcrumbs = (pathname: string, envid: string): string[] => {
  if (!pathname || !envid) {
    return undefined;
  }

  const locationArray = pathname.split('/');
  const envIndec = locationArray.indexOf(envid);
  const breadcrumbs = locationArray.slice(envIndec + 1).filter(e => e !== '');

  return breadcrumbs;
}
